package br.com.tributos.identity.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.identity.domain.Usuario;

public record UsuarioResumoResponse(
    UUID id,
    String login,
    String email
) {

    public static UsuarioResumoResponse de(Usuario usuario) {
        return new UsuarioResumoResponse(usuario.getId(), usuario.getLogin(), usuario.getEmail());
    }
}
