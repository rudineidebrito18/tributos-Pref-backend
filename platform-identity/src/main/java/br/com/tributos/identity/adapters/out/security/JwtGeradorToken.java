package br.com.tributos.identity.adapters.out.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import br.com.tributos.identity.application.ports.GeradorToken;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.kernel.exception.AutenticacaoException;

/**
 * Emite e valida JWT auto-assinados (HS256) — não há Authorization Server externo nesta
 * fase (ver PLANEJAMENTO_PROJETO.md §7.3: "Keycloak, a avaliar no Sprint 0" — decisão
 * adiada; HMAC simétrico resolve o Sprint 0 e a migração para chave assimétrica/Keycloak
 * no futuro não muda a porta {@link GeradorToken}, só esta implementação).
 */
@Component
public class JwtGeradorToken implements GeradorToken {

    private static final Duration DURACAO_ACCESS_TOKEN = Duration.ofMinutes(15);
    private static final Duration DURACAO_TOKEN_MFA_PENDENTE = Duration.ofMinutes(5);
    private static final String CLAIM_PROPOSITO = "proposito";
    private static final String PROPOSITO_MFA_PENDENTE = "mfa_pendente";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final String emissor;
    private final JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

    public JwtGeradorToken(
        @Value("${app.security.jwt.secret}") String segredo,
        @Value("${app.security.jwt.emissor:tributos-backend}") String emissor
    ) {
        if (segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                "app.security.jwt.secret precisa ter pelo menos 32 bytes (HS256) — configure APP_SECURITY_JWT_SECRET.");
        }
        SecretKeySpec chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(chave));
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
        this.emissor = emissor;
    }

    @Override
    public String gerarAccessToken(Usuario usuario, Set<String> papeis) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(emissor)
            .issuedAt(agora)
            .expiresAt(agora.plus(DURACAO_ACCESS_TOKEN))
            .subject(usuario.getId().toString())
            .claim("tenant_id", usuario.getTenantId().toString())
            .claim("roles", papeis)
            .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    @Override
    public Duration duracaoAccessToken() {
        return DURACAO_ACCESS_TOKEN;
    }

    @Override
    public String gerarTokenMfaPendente(UUID usuarioId) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(emissor)
            .issuedAt(agora)
            .expiresAt(agora.plus(DURACAO_TOKEN_MFA_PENDENTE))
            .subject(usuarioId.toString())
            .claim(CLAIM_PROPOSITO, PROPOSITO_MFA_PENDENTE)
            .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    @Override
    public UUID validarTokenMfaPendente(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            if (!PROPOSITO_MFA_PENDENTE.equals(jwt.getClaimAsString(CLAIM_PROPOSITO))) {
                throw new AutenticacaoException("Token inválido para esta operação.");
            }
            return UUID.fromString(jwt.getSubject());
        } catch (JwtException ex) {
            throw new AutenticacaoException("Sessão de verificação MFA inválida ou expirada — faça login novamente.");
        }
    }
}
