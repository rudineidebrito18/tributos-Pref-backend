package br.com.tributos.financeiro.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.tributos.financeiro.application.FiltroFaturamento;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.kernel.tenancy.TenantContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class FaturamentoRelatorioQueryRepository {

    private static final String SELECT_BASE = """
        SELECT g.id,
               g.competencia_mes,
               g.competencia_ano,
               g.numero,
               p.cpf_cnpj,
               COALESCE(NULLIF(c.nome_fantasia, ''), p.nome) AS contribuinte_nome,
               g.situacao,
               fp.nome AS forma_pagamento,
               g.tipo_tributo,
               g.data_emissao,
               g.data_efetivacao,
               g.valor,
               g.valor_pago
        FROM guia_arrecadacao g
        JOIN iss_contribuinte c ON c.id = g.contribuinte_id AND c.tenant_id = g.tenant_id
        JOIN pessoa p ON p.id = c.pessoa_id AND p.tenant_id = g.tenant_id
        LEFT JOIN forma_pagamento fp ON fp.id = g.forma_pagamento_id
        WHERE g.tenant_id = :tenantId
        """;

    @PersistenceContext
    private EntityManager entityManager;

    public Page<FaturamentoLinhaProjection> buscar(FiltroFaturamento filtro, Pageable pageable) {
        UUID tenantId = TenantContext.getObrigatorio();
        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", tenantId);

        StringBuilder where = new StringBuilder();
        aplicarFiltros(filtro, where, params);

        String orderBy = " ORDER BY g.data_emissao DESC, g.numero DESC";
        String sql = SELECT_BASE + where + orderBy;
        String countSql = "SELECT COUNT(*) FROM guia_arrecadacao g "
            + "JOIN iss_contribuinte c ON c.id = g.contribuinte_id AND c.tenant_id = g.tenant_id "
            + "JOIN pessoa p ON p.id = c.pessoa_id AND p.tenant_id = g.tenant_id "
            + "WHERE g.tenant_id = :tenantId " + where;

        Query countQuery = entityManager.createNativeQuery(countSql);
        params.forEach(countQuery::setParameter);
        Number total = (Number) countQuery.getSingleResult();

        Query dataQuery = entityManager.createNativeQuery(sql);
        params.forEach(dataQuery::setParameter);
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dataQuery.getResultList();
        List<FaturamentoLinhaProjection> linhas = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            linhas.add(mapear(row));
        }
        return new PageImpl<>(linhas, pageable, total.longValue());
    }

    public List<FaturamentoLinhaProjection> buscarTodos(FiltroFaturamento filtro, int limite) {
        UUID tenantId = TenantContext.getObrigatorio();
        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", tenantId);

        StringBuilder where = new StringBuilder();
        aplicarFiltros(filtro, where, params);

        String sql = SELECT_BASE + where + " ORDER BY g.data_emissao DESC, g.numero DESC";
        Query dataQuery = entityManager.createNativeQuery(sql);
        params.forEach(dataQuery::setParameter);
        dataQuery.setMaxResults(limite);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dataQuery.getResultList();
        List<FaturamentoLinhaProjection> linhas = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            linhas.add(mapear(row));
        }
        return linhas;
    }

    private static void aplicarFiltros(FiltroFaturamento filtro, StringBuilder where, Map<String, Object> params) {
        if (filtro == null) {
            return;
        }
        if (filtro.pago() != null) {
            if (filtro.pago()) {
                where.append(" AND g.situacao = 'PAGA'");
            } else {
                where.append(" AND g.situacao <> 'PAGA'");
            }
        }
        if (filtro.contribuinteId() != null) {
            where.append(" AND g.contribuinte_id = :contribuinteId");
            params.put("contribuinteId", filtro.contribuinteId());
        }
        if (filtro.pagadorNome() != null && !filtro.pagadorNome().isBlank()) {
            where.append(" AND LOWER(p.nome) LIKE LOWER(:pagadorNome)");
            params.put("pagadorNome", "%" + filtro.pagadorNome().trim() + "%");
        }
        if (filtro.pagadorCpfCnpj() != null && !filtro.pagadorCpfCnpj().isBlank()) {
            where.append(" AND p.cpf_cnpj LIKE :pagadorCpfCnpj");
            params.put("pagadorCpfCnpj", "%" + filtro.pagadorCpfCnpj().replaceAll("\\D", "") + "%");
        }
        if (filtro.competenciaMes() != null) {
            where.append(" AND g.competencia_mes = :competenciaMes");
            params.put("competenciaMes", filtro.competenciaMes());
        }
        if (filtro.competenciaAno() != null) {
            where.append(" AND g.competencia_ano = :competenciaAno");
            params.put("competenciaAno", filtro.competenciaAno());
        }
        if (filtro.statusPix() != null) {
            where.append(" AND g.status_pix = :statusPix");
            params.put("statusPix", filtro.statusPix().name());
        }
        if (filtro.valor() != null) {
            where.append(" AND g.valor = :valor");
            params.put("valor", filtro.valor());
        }
        if (filtro.dataEmissaoInicio() != null) {
            where.append(" AND g.data_emissao >= :dataEmissaoInicio");
            params.put("dataEmissaoInicio", filtro.dataEmissaoInicio().atStartOfDay(java.time.ZoneId.of("America/Sao_Paulo")).toInstant());
        }
        if (filtro.dataEmissaoFim() != null) {
            where.append(" AND g.data_emissao < :dataEmissaoFim");
            params.put("dataEmissaoFim", filtro.dataEmissaoFim().plusDays(1).atStartOfDay(java.time.ZoneId.of("America/Sao_Paulo")).toInstant());
        }
        if (filtro.dataPagamentoInicio() != null) {
            where.append(" AND g.data_efetivacao >= :dataPagamentoInicio");
            params.put("dataPagamentoInicio", filtro.dataPagamentoInicio().atStartOfDay(java.time.ZoneId.of("America/Sao_Paulo")).toInstant());
        }
        if (filtro.dataPagamentoFim() != null) {
            where.append(" AND g.data_efetivacao < :dataPagamentoFim");
            params.put("dataPagamentoFim", filtro.dataPagamentoFim().plusDays(1).atStartOfDay(java.time.ZoneId.of("America/Sao_Paulo")).toInstant());
        }
        if (filtro.codigoConciliacaoSolicitante() != null && !filtro.codigoConciliacaoSolicitante().isBlank()) {
            where.append(" AND g.pix_txid LIKE :codigoConciliacao");
            params.put("codigoConciliacao", "%" + filtro.codigoConciliacaoSolicitante().trim() + "%");
        }
        if (filtro.tipoTributo() != null) {
            where.append(" AND g.tipo_tributo = :tipoTributo");
            params.put("tipoTributo", filtro.tipoTributo().name());
        }
        if (filtro.formaPagamentoId() != null) {
            where.append(" AND g.forma_pagamento_id = :formaPagamentoId");
            params.put("formaPagamentoId", filtro.formaPagamentoId());
        }
    }

    private static FaturamentoLinhaProjection mapear(Object[] row) {
        return new FaturamentoLinhaProjection(
            (UUID) row[0],
            row[1] != null ? ((Number) row[1]).intValue() : null,
            row[2] != null ? ((Number) row[2]).intValue() : null,
            ((Number) row[3]).longValue(),
            (String) row[4],
            (String) row[5],
            SituacaoGuia.valueOf((String) row[6]),
            (String) row[7],
            (String) row[8],
            paraInstant(row[9]),
            paraInstant(row[10]),
            (BigDecimal) row[11],
            (BigDecimal) row[12]
        );
    }

    private static Instant paraInstant(Object valor) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof Instant instant) {
            return instant;
        }
        if (valor instanceof java.sql.Timestamp timestamp) {
            return timestamp.toInstant();
        }
        if (valor instanceof LocalDateTime localDateTime) {
            return localDateTime.atZone(ZoneId.of("America/Sao_Paulo")).toInstant();
        }
        throw new IllegalStateException("Tipo de data não suportado: " + valor.getClass());
    }

    public record FaturamentoLinhaProjection(
        UUID guiaId,
        Integer competenciaMes,
        Integer competenciaAno,
        long numero,
        String cpfCnpj,
        String contribuinte,
        SituacaoGuia situacao,
        String formaPagamento,
        String tipoTributo,
        Instant emissao,
        Instant efetivacao,
        BigDecimal valor,
        BigDecimal valorPago
    ) {
        public String mesAnoVersao() {
            String mes = competenciaMes != null ? String.format("%02d", competenciaMes) : "--";
            String ano = competenciaAno != null ? String.valueOf(competenciaAno) : "----";
            return mes + "/" + ano + "/" + numero;
        }
    }
}
