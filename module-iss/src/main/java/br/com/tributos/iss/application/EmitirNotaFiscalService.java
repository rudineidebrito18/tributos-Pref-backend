package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.CalculadorValorIss;
import br.com.tributos.iss.domain.CatalogoIssRepository;
import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.NotaFiscal;
import br.com.tributos.iss.domain.NotaFiscalEmitidaEvent;
import br.com.tributos.iss.domain.NotaFiscalRepository;
import br.com.tributos.iss.domain.ServicoRepository;
import br.com.tributos.iss.domain.StatusCredenciamentoNomes;
import br.com.tributos.iss.domain.StatusNotaFiscal;
import br.com.tributos.iss.domain.TipoCatalogoIss;
import br.com.tributos.iss.domain.TomadorRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class EmitirNotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final ContribuinteRepository contribuinteRepository;
    private final TomadorRepository tomadorRepository;
    private final ServicoRepository servicoRepository;
    private final CatalogoIssRepository catalogoIssRepository;
    private final CalcularAliquotaEfetivaService calcularAliquotaEfetivaService;
    private final ApplicationEventPublisher eventPublisher;

    public EmitirNotaFiscalService(
        NotaFiscalRepository notaFiscalRepository,
        ContribuinteRepository contribuinteRepository,
        TomadorRepository tomadorRepository,
        ServicoRepository servicoRepository,
        CatalogoIssRepository catalogoIssRepository,
        CalcularAliquotaEfetivaService calcularAliquotaEfetivaService,
        ApplicationEventPublisher eventPublisher
    ) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.contribuinteRepository = contribuinteRepository;
        this.tomadorRepository = tomadorRepository;
        this.servicoRepository = servicoRepository;
        this.catalogoIssRepository = catalogoIssRepository;
        this.calcularAliquotaEfetivaService = calcularAliquotaEfetivaService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public NotaFiscal executar(EmitirNotaFiscalComando comando) {
        Contribuinte contribuinte = contribuinteRepository.buscarPorId(comando.contribuinteId())
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));

        UUID statusAprovadoId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.APROVADO)
            .orElseThrow(() -> new IllegalStateException("Status APROVADO não encontrado no catálogo do tenant."))
            .id();

        if (!contribuinte.statusCredenciamentoId().equals(statusAprovadoId)) {
            throw new ValidationException("O contribuinte precisa estar com credenciamento aprovado para emitir nota fiscal.");
        }

        if (tomadorRepository.buscarPorId(comando.tomadorId()).isEmpty()) {
            throw new NotFoundException("Tomador não encontrado.");
        }

        if (servicoRepository.buscarPorId(comando.servicoId()).isEmpty()) {
            throw new NotFoundException("Serviço não encontrado.");
        }

        if (comando.valorServico() == null || comando.valorServico().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Informe um valor de serviço maior que zero.");
        }

        BigDecimal deducoes = comando.valorDeducoes() != null ? comando.valorDeducoes() : BigDecimal.ZERO;
        if (deducoes.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("O valor de deduções não pode ser negativo.");
        }

        LocalDate competencia = comando.competencia();
        if (competencia == null) {
            throw new ValidationException("Informe a competência da nota fiscal.");
        }

        var aliquota = calcularAliquotaEfetivaService.calcular(
            contribuinte.regimeTributarioId(),
            comando.receitaBrutaAcumulada12Meses(),
            competencia
        );

        CalculadorValorIss.Resultado valores = CalculadorValorIss.calcular(
            comando.valorServico(),
            deducoes,
            aliquota.aliquotaIssEfetiva()
        );

        UUID tenantId = TenantContext.getObrigatorio();
        UUID notaId = UUID.randomUUID();
        long numero = notaFiscalRepository.proximoNumero();
        String serie = comando.serie() != null && !comando.serie().isBlank() ? comando.serie().trim() : "1";
        Instant dataEmissao = Instant.now();

        NotaFiscal nota = new NotaFiscal(
            notaId,
            tenantId,
            numero,
            serie,
            comando.contribuinteId(),
            comando.tomadorId(),
            comando.servicoId(),
            competencia,
            comando.valorServico(),
            deducoes,
            valores.baseCalculo(),
            aliquota.aliquotaIssEfetiva(),
            valores.valorIss(),
            StatusNotaFiscal.EMITIDA,
            null,
            null,
            dataEmissao
        );

        NotaFiscal salva = notaFiscalRepository.salvar(nota);

        eventPublisher.publishEvent(new NotaFiscalEmitidaEvent(
            salva.id(),
            salva.tenantId(),
            salva.contribuinteId(),
            salva.valorIss(),
            salva.dataEmissao()
        ));

        return salva;
    }
}
