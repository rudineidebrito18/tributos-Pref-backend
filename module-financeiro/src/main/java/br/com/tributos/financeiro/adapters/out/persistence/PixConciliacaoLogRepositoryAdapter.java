package br.com.tributos.financeiro.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.domain.PixConciliacaoLog;
import br.com.tributos.financeiro.domain.PixConciliacaoLogRepository;

@Component
public class PixConciliacaoLogRepositoryAdapter implements PixConciliacaoLogRepository {

    private final PixConciliacaoLogJpaRepository jpaRepository;

    public PixConciliacaoLogRepositoryAdapter(PixConciliacaoLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PixConciliacaoLog salvar(PixConciliacaoLog log) {
        return paraDominio(jpaRepository.save(paraEntidade(log)));
    }

    @Override
    public List<PixConciliacaoLog> listarPorGuiaId(UUID guiaId) {
        return jpaRepository.findByGuiaIdOrderByCriadoEmDesc(guiaId).stream()
            .map(this::paraDominio)
            .toList();
    }

    private PixConciliacaoLog paraDominio(PixConciliacaoLogJpaEntity e) {
        return new PixConciliacaoLog(
            e.getId(),
            e.getTenantId(),
            e.getGuiaId(),
            e.getTxid(),
            e.getEndToEndId(),
            e.getStatusAnterior(),
            e.getStatusNovo(),
            e.getOrigem(),
            e.getPayloadBruto(),
            e.getCriadoEm()
        );
    }

    private PixConciliacaoLogJpaEntity paraEntidade(PixConciliacaoLog log) {
        PixConciliacaoLogJpaEntity e = new PixConciliacaoLogJpaEntity();
        e.setId(log.id() != null ? log.id() : UUID.randomUUID());
        e.setTenantId(log.tenantId());
        e.setGuiaId(log.guiaId());
        e.setTxid(log.txid());
        e.setEndToEndId(log.endToEndId());
        e.setStatusAnterior(log.statusAnterior());
        e.setStatusNovo(log.statusNovo());
        e.setOrigem(log.origem());
        e.setPayloadBruto(log.payloadBruto());
        e.setCriadoEm(log.criadoEm() != null ? log.criadoEm() : java.time.Instant.now());
        return e;
    }
}
