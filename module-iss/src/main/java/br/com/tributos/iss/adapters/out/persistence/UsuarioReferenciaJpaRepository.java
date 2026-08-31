package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioReferenciaJpaRepository extends JpaRepository<UsuarioReferenciaJpaEntity, UUID> {

    Optional<UsuarioReferenciaJpaEntity> findByLogin(String login);
}
