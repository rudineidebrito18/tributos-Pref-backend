package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PessoaJpaRepository extends JpaRepository<PessoaJpaEntity, UUID> {

    boolean existsByCpfCnpjAndIdNot(String cpfCnpj, UUID id);

    boolean existsByCpfCnpj(String cpfCnpj);

    @Query("""
        SELECT p FROM PessoaJpaEntity p
        WHERE (:busca IS NULL OR :busca = ''
            OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
            OR p.cpfCnpj LIKE CONCAT('%', :busca, '%'))
        ORDER BY p.nome
        """)
    Page<PessoaJpaEntity> buscarComFiltro(@Param("busca") String busca, Pageable pageable);
}
