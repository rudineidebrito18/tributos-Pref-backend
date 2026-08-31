package br.com.tributos.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorLogin(UUID tenantId, String login);

    Optional<Usuario> buscarPorEmail(UUID tenantId, String email);

    Optional<Usuario> buscarPorId(UUID id);

    void salvar(Usuario usuario);

    /** Nomes dos papéis (RBAC) atribuídos ao usuário — já resolvidos para uso em claims do JWT. */
    Set<String> buscarNomesDosPapeis(UUID usuarioId);

    /** Associa um papel global (ex.: {@code ADMIN_TENANT}) já existente no catálogo ao usuário. */
    void atribuirPapel(UUID usuarioId, String nomeDoPapel);

    List<Usuario> listarAtivosDoTenant(UUID tenantId);

    boolean existeLoginOuEmail(UUID tenantId, String login, String email, UUID excluirUsuarioId);
}
