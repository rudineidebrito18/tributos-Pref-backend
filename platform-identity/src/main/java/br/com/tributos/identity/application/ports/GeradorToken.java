package br.com.tributos.identity.application.ports;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import br.com.tributos.identity.domain.Usuario;

/**
 * Porta de saída para emissão/validação de tokens JWT. A implementação (Nimbus JOSE, via
 * {@code spring-security-oauth2-jose}) mora em {@code adapters.out.security} — o domínio e
 * a camada de aplicação não sabem que os tokens são JWT, só que existem "access token" e
 * "token de desafio MFA" com essas operações.
 */
public interface GeradorToken {

    String gerarAccessToken(Usuario usuario, Set<String> papeis);

    Duration duracaoAccessToken();

    /**
     * Token de curta duração emitido depois que a senha foi validada mas antes do código
     * MFA — correlaciona as duas chamadas do fluxo de login em duas etapas sem precisar de
     * sessão no servidor (a API é stateless).
     */
    String gerarTokenMfaPendente(UUID usuarioId);

    /** @throws br.com.tributos.kernel.exception.AutenticacaoException se inválido/expirado. */
    UUID validarTokenMfaPendente(String token);
}
