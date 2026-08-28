package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.ValorTerrenoM2;
import br.com.tributos.iptu.domain.ValorTerrenoM2Repository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ValorTerrenoM2RepositoryAdapter implements ValorTerrenoM2Repository {

    private final ValorTerrenoM2JpaRepository jpaRepository;

    public ValorTerrenoM2RepositoryAdapter(ValorTerrenoM2JpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ValorTerrenoM2 salvar(ValorTerrenoM2 valor) {
        UUID tenantId = TenantContext.getObrigatorio();
        ValorTerrenoM2JpaEntity entidade = jpaRepository.findByZonaFiscalIdAndExercicio(valor.zonaFiscalId(), valor.exercicio())
            .orElseGet(() -> {
                ValorTerrenoM2JpaEntity nova = new ValorTerrenoM2JpaEntity();
                nova.setId(valor.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setZonaFiscalId(valor.zonaFiscalId());
        entidade.setExercicio(valor.exercicio());
        entidade.setValorM2(valor.valorM2());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<ValorTerrenoM2> buscarPorZonaEExercicio(UUID zonaFiscalId, int exercicio) {
        return jpaRepository.findByZonaFiscalIdAndExercicio(zonaFiscalId, exercicio).map(ValorTerrenoM2RepositoryAdapter::paraDominio);
    }

    @Override
    public List<ValorTerrenoM2> listarPorExercicio(int exercicio) {
        return jpaRepository.findByExercicioOrderByZonaFiscalId(exercicio).stream()
            .map(ValorTerrenoM2RepositoryAdapter::paraDominio)
            .toList();
    }

    private static ValorTerrenoM2 paraDominio(ValorTerrenoM2JpaEntity entidade) {
        return new ValorTerrenoM2(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getZonaFiscalId(),
            entidade.getExercicio(),
            entidade.getValorM2()
        );
    }
}
