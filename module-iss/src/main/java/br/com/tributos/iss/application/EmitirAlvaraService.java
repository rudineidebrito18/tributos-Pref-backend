package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Alvara;
import br.com.tributos.iss.domain.AlvaraRepository;
import br.com.tributos.iss.domain.CatalogoIssRepository;
import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.GeradorCodigoVerificacao;
import br.com.tributos.iss.domain.SanitizadorHtmlIss;
import br.com.tributos.iss.domain.SituacaoFiscalAlvara;
import br.com.tributos.iss.domain.StatusCredenciamentoNomes;
import br.com.tributos.iss.domain.TipoAlvara;
import br.com.tributos.iss.domain.TipoAlvaraRepository;
import br.com.tributos.iss.domain.TipoCatalogoIss;
import br.com.tributos.iss.domain.ValidadorVigenciaDocumento;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class EmitirAlvaraService {

    private final AlvaraRepository alvaraRepository;
    private final TipoAlvaraRepository tipoAlvaraRepository;
    private final ContribuinteRepository contribuinteRepository;
    private final CatalogoIssRepository catalogoIssRepository;

    public EmitirAlvaraService(
        AlvaraRepository alvaraRepository,
        TipoAlvaraRepository tipoAlvaraRepository,
        ContribuinteRepository contribuinteRepository,
        CatalogoIssRepository catalogoIssRepository
    ) {
        this.alvaraRepository = alvaraRepository;
        this.tipoAlvaraRepository = tipoAlvaraRepository;
        this.contribuinteRepository = contribuinteRepository;
        this.catalogoIssRepository = catalogoIssRepository;
    }

    @Transactional
    public Alvara executar(EmitirAlvaraComando comando) {
        Contribuinte contribuinte = contribuinteRepository.buscarPorId(comando.contribuinteId())
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));

        UUID statusAprovadoId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.APROVADO)
            .orElseThrow(() -> new IllegalStateException("Status APROVADO não encontrado no catálogo do tenant."))
            .id();

        if (!contribuinte.statusCredenciamentoId().equals(statusAprovadoId)) {
            throw new ValidationException("O contribuinte precisa estar com credenciamento aprovado para emitir alvará.");
        }

        TipoAlvara tipo = tipoAlvaraRepository.buscarPorId(comando.tipoAlvaraId())
            .orElseThrow(() -> new NotFoundException("Tipo de alvará não encontrado."));

        if (!tipo.ativo()) {
            throw new ValidationException("O tipo de alvará selecionado está inativo.");
        }

        if (comando.dataExpedicao() == null) {
            throw new ValidationException("Informe a data de expedição do alvará.");
        }

        SituacaoFiscalAlvara situacaoFiscal = comando.situacaoFiscal() != null
            ? comando.situacaoFiscal()
            : SituacaoFiscalAlvara.PENDENTE;

        LocalDate validade = comando.validade() != null
            ? comando.validade()
            : dataExpedicao(comando, tipo);

        ValidadorVigenciaDocumento.validarPeriodoAlvara(comando.dataExpedicao(), validade);

        BigDecimal valor = calcularValor(tipo, comando);

        UUID tenantId = TenantContext.getObrigatorio();
        long numero = alvaraRepository.proximoNumero();
        String codigoVerificacao = GeradorCodigoVerificacao.gerar();
        Instant dataEmissao = Instant.now();

        Alvara alvara = new Alvara(
            UUID.randomUUID(),
            tenantId,
            numero,
            comando.tipoAlvaraId(),
            comando.contribuinteId(),
            comando.dataExpedicao(),
            validade,
            situacaoFiscal,
            valor,
            codigoVerificacao,
            dataEmissao,
            comando.valorPorUnidade(),
            comando.unidadeMedidaDescritivo(),
            comando.qtdUnidadeMedida(),
            SanitizadorHtmlIss.sanitizar(comando.documentoHtml()),
            comando.responsavelTecnico(),
            comando.inscricaoConselhoRt(),
            null,
            comando.observacao()
        );

        return alvaraRepository.salvar(alvara);
    }

    private static LocalDate dataExpedicao(EmitirAlvaraComando comando, TipoAlvara tipo) {
        if (tipo.habilitarCalculoVencimento() && tipo.baseVencimento() != null && tipo.diasMesesVencimento() != null) {
            return switch (tipo.baseVencimento()) {
                case MES -> comando.dataExpedicao().plusMonths(tipo.diasMesesVencimento());
                case DIAS -> comando.dataExpedicao().plusDays(tipo.diasMesesVencimento());
            };
        }
        return comando.dataExpedicao().plusDays(tipo.diasValidade());
    }

    static BigDecimal calcularValor(TipoAlvara tipo, EmitirAlvaraComando comando) {
        if (tipo.permiteCalculoValor()) {
            if (comando.valorPorUnidade() == null || comando.qtdUnidadeMedida() == null) {
                throw new ValidationException("Informe valor por unidade e quantidade para cálculo do alvará.");
            }
            return comando.valorPorUnidade().multiply(comando.qtdUnidadeMedida());
        }
        if (comando.valor() != null) {
            return comando.valor();
        }
        return tipo.valorBase();
    }
}
