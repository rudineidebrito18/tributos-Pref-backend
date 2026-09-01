package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImovelProprietarioJpaRepository extends JpaRepository<ImovelProprietarioJpaEntity, UUID> {

    List<ImovelProprietarioJpaEntity> findByImovelIdOrderByProprietarioPrincipalDescCriadoEmAsc(UUID imovelId);

    boolean existsByImovelIdAndContribuinteIdAndIdNot(UUID imovelId, UUID contribuinteId, UUID id);

    void deleteByImovelId(UUID imovelId);
}
