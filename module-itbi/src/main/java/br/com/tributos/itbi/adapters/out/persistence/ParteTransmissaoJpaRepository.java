package br.com.tributos.itbi.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.tributos.itbi.domain.PapelParteTransmissao;

public interface ParteTransmissaoJpaRepository extends JpaRepository<ParteTransmissaoJpaEntity, UUID> {

    List<ParteTransmissaoJpaEntity> findByGuiaIdAndPapelOrderByPrincipalDescPorcentagemDesc(
        UUID guiaId,
        PapelParteTransmissao papel
    );

    boolean existsByGuiaIdAndContribuinteIdAndPapelAndIdNot(
        UUID guiaId,
        UUID contribuinteId,
        PapelParteTransmissao papel,
        UUID id
    );
}
