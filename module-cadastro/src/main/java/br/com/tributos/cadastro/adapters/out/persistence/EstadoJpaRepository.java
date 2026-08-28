package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstadoJpaRepository extends JpaRepository<EstadoJpaEntity, UUID> {

    List<EstadoJpaEntity> findAllByOrderByNomeAsc();
}
