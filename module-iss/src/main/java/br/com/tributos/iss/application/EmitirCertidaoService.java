package br.com.tributos.iss.application;

import java.time.Instant;
import java.time.LocalDate;
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
import br.com.tributos.kernel.exception.NotFoundException;
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
    public CertidaoIss executar(UUID contribuinteId, TipoCertidaoIss tipo, LocalDate validadeOverride) {
        Contribuinte contribuinte = contribuinteRepository.buscarPorId(contribuinteId)
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));

        UUID statusAprovadoId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.APROVADO)
            .orElseThrow(() -> new IllegalStateException("Status APROVADO não encontrado no catálogo do tenant."))
            .id();

        if (!contribuinte.statusCredenciamentoId().equals(statusAprovadoId)) {
            throw new ValidationException("O contribuinte precisa estar com credenciamento aprovado para emitir certidão.");
        }

        if (tipo == null) {
            throw new ValidationException("Informe o tipo da certidão.");
        }

        UUID tenantId = TenantContext.getObrigatorio();

        if (tipo == TipoCertidaoIss.NADA_CONSTA && pendenciaFinanceiraPort.possuiPendencia(tenantId, contribuinte.pessoaId())) {
            throw new ValidationException("Não é possível emitir certidão de nada consta para contribuinte com pendências financeiras.");
        }

        LocalDate validade = validadeOverride != null
            ? validadeOverride
            : LocalDate.now().plusDays(DIAS_VALIDADE_PADRAO);

        long numero = certidaoIssRepository.proximoNumero();
        String codigoVerificacao = GeradorCodigoVerificacao.gerar();
        Instant dataEmissao = Instant.now();

        CertidaoIss certidao = new CertidaoIss(
            UUID.randomUUID(),
            tenantId,
            tipo,
            contribuinteId,
            numero,
            codigoVerificacao,
            dataEmissao,
            validade
        );

        return certidaoIssRepository.salvar(certidao);
    }
}
