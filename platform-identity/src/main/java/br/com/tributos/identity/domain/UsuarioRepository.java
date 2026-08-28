package br.com.tributos.identity.domain;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorLogin(UUID tenantId, String login);

    Optional<Usuario> buscarPorId(UUID id);

    void salvar(Usuario usuario);

    /** Nomes dos papéis (RBAC) atribuídos ao usuário — já resolvidos para uso em claims do JWT. */
    Set<String> buscarNomesDosPapeis(UUID usuarioId);
}
