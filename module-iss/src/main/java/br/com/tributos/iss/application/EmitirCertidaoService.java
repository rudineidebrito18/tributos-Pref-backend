package br.com.tributos.iss.application;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.CatalogoIssRepository;
import br.com.tributos.iss.domain.CertidaoIss;
import br.com.tributos.iss.domain.CertidaoIssRepository;
import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.GeradorCodigoVerificacao;
import br.com.tributos.iss.domain.StatusCredenciamentoNomes;
import br.com.tributos.iss.domain.TipoCatalogoIss;
import br.com.tributos.iss.domain.TipoCertidaoIss;
import br.com.tributos.iss.domain.TributoCertidao;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.RegraNegocioException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.financeiro.PendenciaFinanceiraPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class EmitirCertidaoService {

    private static final int DIAS_VALIDADE_PADRAO = 90;

    private final CertidaoIssRepository certidaoIssRepository;
    private final ContribuinteRepository contribuinteRepository;
    private final CatalogoIssRepository catalogoIssRepository;
    private final PendenciaFinanceiraPort pendenciaFinanceiraPort;

    public EmitirCertidaoService(
        CertidaoIssRepository certidaoIssRepository,
        ContribuinteRepository contribuinteRepository,
        CatalogoIssRepository catalogoIssRepository,
        PendenciaFinanceiraPort pendenciaFinanceiraPort
    ) {
        this.certidaoIssRepository = certidaoIssRepository;
        this.contribuinteRepository = contribuinteRepository;
        this.catalogoIssRepository = catalogoIssRepository;
        this.pendenciaFinanceiraPort = pendenciaFinanceiraPort;
    }

    @Transactional
    public CertidaoIss executar(EmitirCertidaoComando comando) {
        Contribuinte contribuinte = contribuinteRepository.buscarPorId(comando.contribuinteId())
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));

        UUID statusAprovadoId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.APROVADO)
            .orElseThrow(() -> new IllegalStateException("Status APROVADO não encontrado no catálogo do tenant."))
            .id();

        if (!contribuinte.statusCredenciamentoId().equals(statusAprovadoId)) {
            throw new ValidationException("O contribuinte precisa estar com credenciamento aprovado para emitir certidão.");
        }

        TipoCertidaoIss tipo = comando.tipo();
        if (tipo == null) {
            throw new ValidationException("Informe o tipo da certidão.");
        }

        List<TributoCertidao> tributos = comando.tributos() != null ? comando.tributos() : List.of();
        if (tributos.isEmpty()) {
            throw new ValidationException("Selecione ao menos um tributo para a certidão.");
        }

        UUID tenantId = TenantContext.getObrigatorio();

        if (comando.avulsa()) {
            if (comando.observacao() == null || comando.observacao().isBlank()) {
                throw new ValidationException("Certidão avulsa exige justificativa em observação.");
            }
        } else if (tipo == TipoCertidaoIss.NADA_CONSTA) {
            validarPendencias(tenantId, contribuinte.pessoaId(), tributos);
        }

        LocalDate validade = comando.validade() != null
            ? comando.validade()
            : LocalDate.now().plusDays(DIAS_VALIDADE_PADRAO);

        long numero = certidaoIssRepository.proximoNumero();
        String codigoVerificacao = GeradorCodigoVerificacao.gerar();
        Instant dataEmissao = Instant.now();

        CertidaoIss certidao = new CertidaoIss(
            UUID.randomUUID(),
            tenantId,
            tipo,
            comando.contribuinteId(),
            numero,
            codigoVerificacao,
            dataEmissao,
            validade,
            comando.situacaoCndId(),
            comando.observacao(),
            comando.avulsa(),
            tributos
        );

        return certidaoIssRepository.salvar(certidao);
    }

    /** Compatibilidade com endpoint legado {@code /emitir}. */
    @Transactional
    public CertidaoIss executar(UUID contribuinteId, TipoCertidaoIss tipo, LocalDate validadeOverride) {
        return executar(new EmitirCertidaoComando(
            contribuinteId,
            tipo,
            validadeOverride,
            null,
            null,
            false,
            List.of(TributoCertidao.ISS)
        ));
    }

    private void validarPendencias(UUID tenantId, UUID pessoaId, List<TributoCertidao> tributos) {
        for (TributoCertidao tributo : tributos) {
            if (pendenciaFinanceiraPort.possuiPendenciaTributo(tenantId, pessoaId, tributo.name())) {
                throw new RegraNegocioException(
                    "Não é possível emitir certidão: existem pendências financeiras para o tributo " + tributo.descricao() + "."
                );
            }
        }
    }
}
