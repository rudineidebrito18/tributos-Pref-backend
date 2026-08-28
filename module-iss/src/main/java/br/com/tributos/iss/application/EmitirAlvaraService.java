package br.com.tributos.iss.application;

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
    public Alvara executar(
        UUID contribuinteId,
        UUID tipoAlvaraId,
        LocalDate dataExpedicao,
        SituacaoFiscalAlvara situacaoFiscal,
        LocalDate validadeOverride
    ) {
        Contribuinte contribuinte = contribuinteRepository.buscarPorId(contribuinteId)
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));

        UUID statusAprovadoId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.APROVADO)
            .orElseThrow(() -> new IllegalStateException("Status APROVADO não encontrado no catálogo do tenant."))
            .id();

        if (!contribuinte.statusCredenciamentoId().equals(statusAprovadoId)) {
            throw new ValidationException("O contribuinte precisa estar com credenciamento aprovado para emitir alvará.");
        }

        TipoAlvara tipo = tipoAlvaraRepository.buscarPorId(tipoAlvaraId)
            .orElseThrow(() -> new NotFoundException("Tipo de alvará não encontrado."));

        if (!tipo.ativo()) {
            throw new ValidationException("O tipo de alvará selecionado está inativo.");
        }

        if (dataExpedicao == null) {
            throw new ValidationException("Informe a data de expedição do alvará.");
        }

        if (situacaoFiscal == null) {
            throw new ValidationException("Informe a situação fiscal do alvará.");
        }

        LocalDate validade = validadeOverride != null
            ? validadeOverride
            : dataExpedicao.plusDays(tipo.diasValidade());

        ValidadorVigenciaDocumento.validarPeriodoAlvara(dataExpedicao, validade);

        UUID tenantId = TenantContext.getObrigatorio();
        long numero = alvaraRepository.proximoNumero();
        String codigoVerificacao = GeradorCodigoVerificacao.gerar();
        Instant dataEmissao = Instant.now();

        Alvara alvara = new Alvara(
            UUID.randomUUID(),
            tenantId,
            numero,
            tipoAlvaraId,
            contribuinteId,
            dataExpedicao,
            validade,
            situacaoFiscal,
            tipo.valorBase(),
            codigoVerificacao,
            dataEmissao
        );

        return alvaraRepository.salvar(alvara);
    }
}
