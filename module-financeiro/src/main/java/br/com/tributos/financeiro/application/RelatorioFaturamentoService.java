package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.tributos.financeiro.adapters.out.persistence.GuiaArrecadacaoJpaEntity;
import br.com.tributos.financeiro.adapters.out.persistence.GuiaArrecadacaoJpaRepository;
import br.com.tributos.financeiro.domain.TipoTributo;

@Service
public class RelatorioFaturamentoService {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final GuiaArrecadacaoJpaRepository jpaRepository;

    public RelatorioFaturamentoService(GuiaArrecadacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public RelatorioFaturamentoResult executar(LocalDate de, LocalDate ate, TipoTributo tipoTributo) {
        Instant inicio = de.atStartOfDay(FUSO).toInstant();
        Instant fim = ate.plusDays(1).atStartOfDay(FUSO).toInstant();

        List<GuiaArrecadacaoJpaEntity> guias = jpaRepository.buscarPagasNoPeriodo(inicio, fim, tipoTributo);

        var porTributo = new java.util.EnumMap<TipoTributo, BigDecimal>(TipoTributo.class);
        BigDecimal total = BigDecimal.ZERO;
        for (GuiaArrecadacaoJpaEntity g : guias) {
            BigDecimal v = g.getValorPago() != null ? g.getValorPago() : g.getValor();
            porTributo.merge(g.getTipoTributo(), v, BigDecimal::add);
            total = total.add(v);
        }

        List<LinhaRelatorio> linhas = new ArrayList<>();
        porTributo.forEach((tipo, valor) -> linhas.add(new LinhaRelatorio(tipo.name(), valor)));

        return new RelatorioFaturamentoResult(de, ate, total, linhas, guias.size());
    }

    public record LinhaRelatorio(String tipoTributo, BigDecimal valor) {
    }

    public record RelatorioFaturamentoResult(
        LocalDate de,
        LocalDate ate,
        BigDecimal totalArrecadado,
        List<LinhaRelatorio> porTributo,
        int quantidadeGuias
    ) {
    }
}
