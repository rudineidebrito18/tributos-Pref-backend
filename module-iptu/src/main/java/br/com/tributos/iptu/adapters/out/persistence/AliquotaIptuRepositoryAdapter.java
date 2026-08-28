package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.AliquotaIptu;
import br.com.tributos.iptu.domain.AliquotaIptuRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class AliquotaIptuRepositoryAdapter implements AliquotaIptuRepository {

    private final AliquotaIptuJpaRepository jpaRepository;

    public AliquotaIptuRepositoryAdapter(AliquotaIptuJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AliquotaIptu salvar(AliquotaIptu aliquota) {
        UUID tenantId = TenantContext.getObrigatorio();
        AliquotaIptuJpaEntity entidade = jpaRepository.findByExercicioAndDestinacaoIdAndZonaFiscalId(
            aliquota.exercicio(),
            aliquota.destinacaoId(),
            aliquota.zonaFiscalId()
        ).orElseGet(() -> {
            AliquotaIptuJpaEntity nova = new AliquotaIptuJpaEntity();
            nova.setId(aliquota.id());
            nova.setTenantId(tenantId);
            return nova;
        });

        entidade.setExercicio(aliquota.exercicio());
        entidade.setDestinacaoId(aliquota.destinacaoId());
        entidade.setZonaFiscalId(aliquota.zonaFiscalId());
        entidade.setAliquota(aliquota.aliquota());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<AliquotaIptu> buscarPorChave(int exercicio, UUID destinacaoId, UUID zonaFiscalId) {
        return jpaRepository.findByExercicioAndDestinacaoIdAndZonaFiscalId(exercicio, destinacaoId, zonaFiscalId)
            .map(AliquotaIptuRepositoryAdapter::paraDominio);
    }

    @Override
    public List<AliquotaIptu> listarPorExercicio(int exercicio) {
        return jpaRepository.findByExercicioOrderByDestinacaoIdAscZonaFiscalIdAsc(exercicio).stream()
            .map(AliquotaIptuRepositoryAdapter::paraDominio)
            .toList();
    }

    private static AliquotaIptu paraDominio(AliquotaIptuJpaEntity entidade) {
        return new AliquotaIptu(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getExercicio(),
            entidade.getDestinacaoId(),
            entidade.getZonaFiscalId(),
            entidade.getAliquota()
        );
    }
}
