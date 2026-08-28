package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TomadorJpaRepository extends JpaRepository<TomadorJpaEntity, UUID> {

    boolean existsByPessoaId(UUID pessoaId);

    boolean existsByPessoaIdAndIdNot(UUID pessoaId, UUID id);

    Page<TomadorJpaEntity> findAllByOrderByCriadoEmDesc(Pageable pageable);
}
