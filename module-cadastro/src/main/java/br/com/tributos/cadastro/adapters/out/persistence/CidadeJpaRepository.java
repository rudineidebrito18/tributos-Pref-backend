package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CidadeJpaRepository extends JpaRepository<CidadeJpaEntity, UUID> {

    @Query("""
        SELECT c FROM CidadeJpaEntity c
        JOIN FETCH c.estado e
        WHERE e.sigla = :uf
        ORDER BY c.nome
        """)
    List<CidadeJpaEntity> findByEstadoSigla(@Param("uf") String uf);
}
