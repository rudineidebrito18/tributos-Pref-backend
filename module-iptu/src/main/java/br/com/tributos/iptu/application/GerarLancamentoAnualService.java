package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.AliquotaIptuRepository;
import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.LancamentoIptu;
import br.com.tributos.iptu.domain.LancamentoIptuRepository;
import br.com.tributos.iptu.domain.LancamentoParcela;
import br.com.tributos.iptu.domain.LancamentoParcelaRepository;
import br.com.tributos.iptu.domain.StatusLancamentoIptu;
import br.com.tributos.iptu.domain.StatusParcelaIptu;
import br.com.tributos.kernel.events.LancamentoIptuParcelaGeradaEvent;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerarLancamentoAnualService {

    private static final int PARCELAS_PADRAO = 10;
    private static final int MES_INICIO_PARCELA = 3;

    private final VerificarParametrizacaoExercicioService verificarParametrizacaoExercicioService;
    private final ImovelRepository imovelRepository;
    private final AliquotaIptuRepository aliquotaIptuRepository;
    private final LancamentoIptuRepository lancamentoIptuRepository;
    private final LancamentoParcelaRepository lancamentoParcelaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GerarLancamentoAnualService(
        VerificarParametrizacaoExercicioService verificarParametrizacaoExercicioService,
        ImovelRepository imovelRepository,
        AliquotaIptuRepository aliquotaIptuRepository,
        LancamentoIptuRepository lancamentoIptuRepository,
        LancamentoParcelaRepository lancamentoParcelaRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.verificarParametrizacaoExercicioService = verificarParametrizacaoExercicioService;
        this.imovelRepository = imovelRepository;
        this.aliquotaIptuRepository = aliquotaIptuRepository;
        this.lancamentoIptuRepository = lancamentoIptuRepository;
        this.lancamentoParcelaRepository = lancamentoParcelaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public List<LancamentoIptu> executar(int exercicio, Integer numeroParcelas) {
        var status = verificarParametrizacaoExercicioService.executar(exercicio);
        if (!status.completo()) {
            throw new ValidationException("Parametrização incompleta para o exercício. Verifique o status antes de gerar lançamentos.");
        }

        int numParcelas = numeroParcelas != null ? numeroParcelas : PARCELAS_PADRAO;
        if (numParcelas <= 0) {
            throw new ValidationException("O número de parcelas deve ser maior que zero.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        List<LancamentoIptu> gerados = new ArrayList<>();

        for (Imovel imovel : imovelRepository.listarAtivosComZonaEDestinacao()) {
            if (lancamentoIptuRepository.buscarPorImovelEExercicio(imovel.id(), exercicio).isPresent()) {
                continue;
            }

            BigDecimal aliquota = aliquotaIptuRepository.buscarPorChave(exercicio, imovel.destinacaoId(), imovel.zonaFiscalId())
                .orElseThrow(() -> new ValidationException("Alíquota não encontrada para o imóvel " + imovel.numeroCadastro()))
                .aliquota();

            BigDecimal valorVenal = CalculadorIptu.calcularValorVenal(imovel);
            BigDecimal valorTotal = CalculadorIptu.calcularValorIptu(valorVenal, aliquota);

            UUID lancamentoId = UUID.randomUUID();
            LancamentoIptu lancamento = new LancamentoIptu(
                lancamentoId,
                tenantId,
                imovel.id(),
                exercicio,
                valorVenal,
                aliquota,
                valorTotal,
                numParcelas,
                StatusLancamentoIptu.GERADO,
                Instant.now()
            );
            LancamentoIptu salvo = lancamentoIptuRepository.salvar(lancamento);
            List<LancamentoParcela> parcelasGeradas = criarParcelas(lancamentoId, tenantId, exercicio, valorTotal, numParcelas);
            lancamentoParcelaRepository.salvarTodos(parcelasGeradas);
            for (LancamentoParcela parcela : parcelasGeradas) {
                eventPublisher.publishEvent(new LancamentoIptuParcelaGeradaEvent(
                    parcela.id(),
                    tenantId,
                    imovel.proprietarioId(),
                    lancamentoId,
                    imovel.id(),
                    exercicio,
                    parcela.numeroParcela(),
                    parcela.valor(),
                    parcela.vencimento()
                ));
            }
            gerados.add(salvo);
        }

        return gerados;
    }

    private static List<LancamentoParcela> criarParcelas(
        UUID lancamentoId,
        UUID tenantId,
        int exercicio,
        BigDecimal valorTotal,
        int numeroParcelas
    ) {
        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.HALF_UP);
        BigDecimal somaParcelas = valorParcela.multiply(BigDecimal.valueOf(numeroParcelas));
        BigDecimal diferenca = valorTotal.subtract(somaParcelas);

        List<LancamentoParcela> parcelas = new ArrayList<>();
        for (int i = 1; i <= numeroParcelas; i++) {
            BigDecimal valor = valorParcela;
            if (i == numeroParcelas) {
                valor = valor.add(diferenca);
            }
            LocalDate vencimento = LocalDate.of(exercicio, MES_INICIO_PARCELA, 1).plusMonths(i - 1);
            parcelas.add(new LancamentoParcela(
                UUID.randomUUID(),
                tenantId,
                lancamentoId,
                i,
                valor,
                vencimento,
                StatusParcelaIptu.PENDENTE
            ));
        }
        return parcelas;
    }
}
