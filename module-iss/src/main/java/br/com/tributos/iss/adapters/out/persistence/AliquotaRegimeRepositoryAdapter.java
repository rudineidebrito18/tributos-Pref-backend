package br.com.tributos.iss.adapters.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.AliquotaRegime;
import br.com.tributos.iss.domain.AliquotaRegimeRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class AliquotaRegimeRepositoryAdapter implements AliquotaRegimeRepository {

    private final AliquotaRegimeJpaRepository jpaRepository;

    public AliquotaRegimeRepositoryAdapter(AliquotaRegimeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<AliquotaRegime> listarPorRegime(UUID regimeId) {
        return jpaRepository.findByRegimeIdOrderByFaixaReceitaMinAsc(regimeId).stream()
            .map(AliquotaRegimeRepositoryAdapter::paraDominio)
            .toList();
    }

    @Override
    public List<AliquotaRegime> listarVigentesPorRegime(UUID regimeId, LocalDate competencia) {
        return jpaRepository.findVigentesPorRegime(regimeId, competencia).stream()
            .map(AliquotaRegimeRepositoryAdapter::paraDominio)
            .toList();
    }

    @Override
    public Optional<AliquotaRegime> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(AliquotaRegimeRepositoryAdapter::paraDominio);
    }

    @Override
    public AliquotaRegime salvar(AliquotaRegime faixa) {
        UUID tenantId = TenantContext.getObrigatorio();
        AliquotaRegimeJpaEntity entidade = jpaRepository.findById(faixa.id())
            .orElseGet(() -> {
                AliquotaRegimeJpaEntity nova = new AliquotaRegimeJpaEntity();
                nova.setId(faixa.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setRegimeId(faixa.regimeId());
        entidade.setFaixaReceitaMin(faixa.faixaReceitaMin());
        entidade.setFaixaReceitaMax(faixa.faixaReceitaMax());
        entidade.setAliquotaNominal(faixa.aliquotaNominal());
        entidade.setParcelaDeduzir(faixa.parcelaDeduzir());
        entidade.setPercentualIss(faixa.percentualIss());
        entidade.setCompetenciaVigencia(faixa.competenciaVigencia());
        entidade.setAnexoSimples(faixa.anexoSimples());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    private static AliquotaRegime paraDominio(AliquotaRegimeJpaEntity entidade) {
        return new AliquotaRegime(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getRegimeId(),
            entidade.getFaixaReceitaMin(),
            entidade.getFaixaReceitaMax(),
            entidade.getAliquotaNominal(),
            entidade.getParcelaDeduzir(),
            entidade.getPercentualIss(),
            entidade.getCompetenciaVigencia(),
            entidade.getAnexoSimples()
        );
    }
}
