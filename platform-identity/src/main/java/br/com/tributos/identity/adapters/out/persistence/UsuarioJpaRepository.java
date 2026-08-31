package br.com.tributos.identity.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

    Optional<UsuarioJpaEntity> findByTenantIdAndLogin(UUID tenantId, String login);

    Optional<UsuarioJpaEntity> findByTenantIdAndEmail(UUID tenantId, String email);

    List<UsuarioJpaEntity> findByTenantIdAndAtivoTrueOrderByLoginAsc(UUID tenantId);

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM UsuarioJpaEntity u
        WHERE u.tenantId = :tenantId
          AND u.id <> :excluirUsuarioId
          AND (u.login = :login OR u.email = :email)
        """)
    boolean existsLoginOuEmail(
        @Param("tenantId") UUID tenantId,
        @Param("login") String login,
        @Param("email") String email,
        @Param("excluirUsuarioId") UUID excluirUsuarioId
    );
}
