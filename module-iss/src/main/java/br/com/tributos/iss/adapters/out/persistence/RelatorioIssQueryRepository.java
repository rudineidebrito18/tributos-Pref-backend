package br.com.tributos.iss.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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

import br.com.tributos.kernel.tenancy.TenantContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class RelatorioIssQueryRepository {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    @PersistenceContext
    private EntityManager entityManager;

    public Page<IrpfLinhaProjection> buscarIrpf(UUID contribuinteId, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        UUID tenantId = TenantContext.getObrigatorio();
        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", tenantId);

        StringBuilder where = new StringBuilder(" WHERE n.tenant_id = :tenantId AND n.valor_ir > 0");
        if (contribuinteId != null) {
            where.append(" AND n.contribuinte_id = :contribuinteId");
            params.put("contribuinteId", contribuinteId);
        }
        if (dataInicio != null) {
            where.append(" AND n.data_emissao >= :dataInicio");
            params.put("dataInicio", dataInicio.atStartOfDay(FUSO).toInstant());
        }
        if (dataFim != null) {
            where.append(" AND n.data_emissao < :dataFim");
            params.put("dataFim", dataFim.plusDays(1).atStartOfDay(FUSO).toInstant());
        }

        String select = """
            SELECT n.numero,
                   COALESCE(NULLIF(c.nome_fantasia, ''), p.nome) AS contribuinte,
                   n.data_emissao,
                   n.valor_ir
            FROM iss_nota_fiscal n
            JOIN iss_contribuinte c ON c.id = n.contribuinte_id AND c.tenant_id = n.tenant_id
            JOIN pessoa p ON p.id = c.pessoa_id AND p.tenant_id = n.tenant_id
            """ + where + " ORDER BY n.data_emissao DESC, n.numero DESC";

        String countSql = "SELECT COUNT(*) FROM iss_nota_fiscal n " + where;
        Query countQuery = entityManager.createNativeQuery(countSql);
        params.forEach(countQuery::setParameter);
        Number total = (Number) countQuery.getSingleResult();

        Query dataQuery = entityManager.createNativeQuery(select);
        params.forEach(dataQuery::setParameter);
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(mapearIrpf(dataQuery.getResultList()), pageable, total.longValue());
    }

    public BigDecimal somarIrpf(UUID contribuinteId, LocalDate dataInicio, LocalDate dataFim) {
        UUID tenantId = TenantContext.getObrigatorio();
        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", tenantId);

        StringBuilder where = new StringBuilder(" WHERE n.tenant_id = :tenantId AND n.valor_ir > 0");
        if (contribuinteId != null) {
            where.append(" AND n.contribuinte_id = :contribuinteId");
            params.put("contribuinteId", contribuinteId);
        }
        if (dataInicio != null) {
            where.append(" AND n.data_emissao >= :dataInicio");
            params.put("dataInicio", dataInicio.atStartOfDay(FUSO).toInstant());
        }
        if (dataFim != null) {
            where.append(" AND n.data_emissao < :dataFim");
            params.put("dataFim", dataFim.plusDays(1).atStartOfDay(FUSO).toInstant());
        }

        String sql = "SELECT COALESCE(SUM(n.valor_ir), 0) FROM iss_nota_fiscal n " + where;
        Query query = entityManager.createNativeQuery(sql);
        params.forEach(query::setParameter);
        return (BigDecimal) query.getSingleResult();
    }

    public List<IrpfLinhaProjection> buscarIrpfTodos(UUID contribuinteId, LocalDate dataInicio, LocalDate dataFim, int limite) {
        UUID tenantId = TenantContext.getObrigatorio();
        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", tenantId);

        StringBuilder where = new StringBuilder(" WHERE n.tenant_id = :tenantId AND n.valor_ir > 0");
        if (contribuinteId != null) {
            where.append(" AND n.contribuinte_id = :contribuinteId");
            params.put("contribuinteId", contribuinteId);
        }
        if (dataInicio != null) {
            where.append(" AND n.data_emissao >= :dataInicio");
            params.put("dataInicio", dataInicio.atStartOfDay(FUSO).toInstant());
        }
        if (dataFim != null) {
            where.append(" AND n.data_emissao < :dataFim");
            params.put("dataFim", dataFim.plusDays(1).atStartOfDay(FUSO).toInstant());
        }

        String select = """
            SELECT n.numero,
                   COALESCE(NULLIF(c.nome_fantasia, ''), p.nome) AS contribuinte,
                   n.data_emissao,
                   n.valor_ir
            FROM iss_nota_fiscal n
            JOIN iss_contribuinte c ON c.id = n.contribuinte_id AND c.tenant_id = n.tenant_id
            JOIN pessoa p ON p.id = c.pessoa_id AND p.tenant_id = n.tenant_id
            """ + where + " ORDER BY n.data_emissao DESC, n.numero DESC";

        Query dataQuery = entityManager.createNativeQuery(select);
        params.forEach(dataQuery::setParameter);
        dataQuery.setMaxResults(limite);
        return mapearIrpf(dataQuery.getResultList());
    }

    public Page<NotasTomadorLinhaProjection> buscarNotasPorTomador(
        UUID tomadorId,
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    ) {
        UUID tenantId = TenantContext.getObrigatorio();
        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", tenantId);
        params.put("tomadorId", tomadorId);

        StringBuilder where = new StringBuilder(" WHERE n.tenant_id = :tenantId AND n.tomador_id = :tomadorId");
        if (dataInicio != null) {
            where.append(" AND n.data_emissao >= :dataInicio");
            params.put("dataInicio", dataInicio.atStartOfDay(FUSO).toInstant());
        }
        if (dataFim != null) {
            where.append(" AND n.data_emissao < :dataFim");
            params.put("dataFim", dataFim.plusDays(1).atStartOfDay(FUSO).toInstant());
        }

        String select = """
            SELECT n.numero,
                   COALESCE(NULLIF(c.nome_fantasia, ''), p.nome) AS contribuinte,
                   COALESCE(a.descricao, '') AS atividade,
                   s.descricao AS servico,
                   n.data_emissao,
                   n.valor_servico,
                   n.valor_iss
            FROM iss_nota_fiscal n
            JOIN iss_contribuinte c ON c.id = n.contribuinte_id AND c.tenant_id = n.tenant_id
            JOIN pessoa p ON p.id = c.pessoa_id AND p.tenant_id = n.tenant_id
            JOIN iss_servico s ON s.id = n.servico_id AND s.tenant_id = n.tenant_id
            LEFT JOIN LATERAL (
                SELECT a2.descricao
                FROM iss_atividade_servico asv
                JOIN iss_atividade a2 ON a2.id = asv.atividade_id AND a2.tenant_id = n.tenant_id
                WHERE asv.servico_id = n.servico_id AND asv.tenant_id = n.tenant_id
                LIMIT 1
            ) a ON TRUE
            """ + where + " ORDER BY n.data_emissao DESC, n.numero DESC";

        String countSql = "SELECT COUNT(*) FROM iss_nota_fiscal n " + where;
        Query countQuery = entityManager.createNativeQuery(countSql);
        params.forEach(countQuery::setParameter);
        Number total = (Number) countQuery.getSingleResult();

        Query dataQuery = entityManager.createNativeQuery(select);
        params.forEach(dataQuery::setParameter);
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(mapearNotasTomador(dataQuery.getResultList()), pageable, total.longValue());
    }

    public List<NotasTomadorLinhaProjection> buscarNotasPorTomadorTodos(
        UUID tomadorId,
        LocalDate dataInicio,
        LocalDate dataFim,
        int limite
    ) {
        UUID tenantId = TenantContext.getObrigatorio();
        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", tenantId);
        params.put("tomadorId", tomadorId);

        StringBuilder where = new StringBuilder(" WHERE n.tenant_id = :tenantId AND n.tomador_id = :tomadorId");
        if (dataInicio != null) {
            where.append(" AND n.data_emissao >= :dataInicio");
            params.put("dataInicio", dataInicio.atStartOfDay(FUSO).toInstant());
        }
        if (dataFim != null) {
            where.append(" AND n.data_emissao < :dataFim");
            params.put("dataFim", dataFim.plusDays(1).atStartOfDay(FUSO).toInstant());
        }

        String select = """
            SELECT n.numero,
                   COALESCE(NULLIF(c.nome_fantasia, ''), p.nome) AS contribuinte,
                   COALESCE(a.descricao, '') AS atividade,
                   s.descricao AS servico,
                   n.data_emissao,
                   n.valor_servico,
                   n.valor_iss
            FROM iss_nota_fiscal n
            JOIN iss_contribuinte c ON c.id = n.contribuinte_id AND c.tenant_id = n.tenant_id
            JOIN pessoa p ON p.id = c.pessoa_id AND p.tenant_id = n.tenant_id
            JOIN iss_servico s ON s.id = n.servico_id AND s.tenant_id = n.tenant_id
            LEFT JOIN LATERAL (
                SELECT a2.descricao
                FROM iss_atividade_servico asv
                JOIN iss_atividade a2 ON a2.id = asv.atividade_id AND a2.tenant_id = n.tenant_id
                WHERE asv.servico_id = n.servico_id AND asv.tenant_id = n.tenant_id
                LIMIT 1
            ) a ON TRUE
            """ + where + " ORDER BY n.data_emissao DESC, n.numero DESC";

        Query dataQuery = entityManager.createNativeQuery(select);
        params.forEach(dataQuery::setParameter);
        dataQuery.setMaxResults(limite);
        return mapearNotasTomador(dataQuery.getResultList());
    }

    public boolean tomadorPertenceAoTenant(UUID tomadorId) {
        UUID tenantId = TenantContext.getObrigatorio();
        Number count = (Number) entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM iss_tomador t WHERE t.id = :tomadorId AND t.tenant_id = :tenantId"
        )
            .setParameter("tomadorId", tomadorId)
            .setParameter("tenantId", tenantId)
            .getSingleResult();
        return count.longValue() > 0;
    }

    @SuppressWarnings("unchecked")
    private static List<IrpfLinhaProjection> mapearIrpf(List<?> rows) {
        List<IrpfLinhaProjection> linhas = new ArrayList<>();
        for (Object rowObj : rows) {
            Object[] row = (Object[]) rowObj;
            linhas.add(new IrpfLinhaProjection(
                ((Number) row[0]).longValue(),
                (String) row[1],
                paraInstant(row[2]),
                (BigDecimal) row[3]
            ));
        }
        return linhas;
    }

    @SuppressWarnings("unchecked")
    private static List<NotasTomadorLinhaProjection> mapearNotasTomador(List<?> rows) {
        List<NotasTomadorLinhaProjection> linhas = new ArrayList<>();
        for (Object rowObj : rows) {
            Object[] row = (Object[]) rowObj;
            linhas.add(new NotasTomadorLinhaProjection(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                paraInstant(row[4]),
                (BigDecimal) row[5],
                (BigDecimal) row[6]
            ));
        }
        return linhas;
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
            return localDateTime.atZone(FUSO).toInstant();
        }
        throw new IllegalStateException("Tipo de data não suportado: " + valor.getClass());
    }

    public record IrpfLinhaProjection(long numeroNota, String contribuinte, Instant dataEmissao, BigDecimal valorIr) {
    }

    public record NotasTomadorLinhaProjection(
        long numeroNota,
        String contribuinte,
        String atividade,
        String servico,
        Instant dataEmissao,
        BigDecimal valorServico,
        BigDecimal valorIss
    ) {
    }
}
