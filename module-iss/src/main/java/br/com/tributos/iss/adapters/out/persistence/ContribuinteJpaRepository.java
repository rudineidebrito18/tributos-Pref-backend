package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContribuinteJpaRepository extends JpaRepository<ContribuinteJpaEntity, UUID> {

    boolean existsByInscricaoMunicipalAndIdNot(String inscricaoMunicipal, UUID id);

    boolean existsByInscricaoMunicipal(String inscricaoMunicipal);

    boolean existsByPessoaIdAndIdNot(UUID pessoaId, UUID id);

    boolean existsByPessoaId(UUID pessoaId);

    @Query("""
        SELECT c FROM ContribuinteJpaEntity c
        WHERE (:busca IS NULL OR :busca = ''
            OR LOWER(c.inscricaoMunicipal) LIKE LOWER(CONCAT('%', :busca, '%')))
        ORDER BY c.inscricaoMunicipal
        """)
    Page<ContribuinteJpaEntity> buscarComFiltro(@Param("busca") String busca, Pageable pageable);
}
