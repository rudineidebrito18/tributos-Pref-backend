package br.com.tributos.identity.adapters.out.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Resolve o id do usuário autenticado a partir do claim {@code sub} do JWT
 * (ver {@link JwtGeradorToken#gerarAccessToken}).
 */
@Component
public class UsuarioAutenticadoResolver {

    public Optional<UUID> usuarioIdAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return Optional.of(UUID.fromString(jwtAuthentication.getToken().getSubject()));
        }
        return Optional.empty();
    }
}
