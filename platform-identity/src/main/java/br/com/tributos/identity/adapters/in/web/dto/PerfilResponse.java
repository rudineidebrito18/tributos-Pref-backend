package br.com.tributos.identity.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.identity.domain.Usuario;

public record PerfilResponse(
    UUID id,
    String nome,
    String login,
    String email,
    UUID fotoDocumentoId
) {

    public static PerfilResponse de(Usuario usuario) {
        return new PerfilResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getLogin(),
            usuario.getEmail(),
            usuario.getFotoDocumentoId()
        );
    }
}
