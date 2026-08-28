package br.com.tributos.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.tributos.kernel.tenancy.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Preenche {@link TenantContext} a partir da claim {@code tenant_id} do JWT já validado —
 * por isso precisa estar depois do {@code BearerTokenAuthenticationFilter} na cadeia (ver
 * {@code SecurityConfig.filterChain}), e não é registrado como {@code Filter} genérico do
 * container (que rodaria antes da autenticação do Spring Security).
 *
 * <p>{@link TenantContext#clear()} no {@code finally}: mesmo com o pool de threads do
 * Tomcat, uma requisição nunca deve "herdar" o tenant de uma requisição anterior atendida
 * pela mesma thread.
 */
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    private static final String CLAIM_TENANT_ID = "tenant_id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
                String tenantIdClaim = jwtAuthentication.getToken().getClaimAsString(CLAIM_TENANT_ID);
                if (tenantIdClaim != null) {
                    TenantContext.set(UUID.fromString(tenantIdClaim));
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
