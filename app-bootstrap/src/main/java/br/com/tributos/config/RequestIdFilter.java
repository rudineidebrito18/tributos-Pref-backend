package br.com.tributos.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Correlaciona todas as linhas de log de uma requisição com um {@code requestId} — a base
 * de "logs estruturados" do Sprint 0 (ver ROADMAP_DESENVOLVIMENTO.md). Aceita
 * {@code X-Request-Id} vindo de um proxy/gateway (rastreamento ponta a ponta) ou gera um
 * novo; devolve o valor usado no header de resposta para o cliente conseguir citá-lo ao
 * reportar um problema.
 *
 * <p>{@code @Order(HIGHEST_PRECEDENCE)}: precisa rodar ANTES da cadeia do Spring Security
 * (registrada pelo {@code DelegatingFilterProxy} em ordem bem menor que a padrão de
 * filtros de aplicação), para que o {@code requestId} já exista no MDC mesmo quando a
 * requisição é rejeitada pela autenticação/autorização — foi exatamente a falta desse fio
 * condutor que tornou mais lento diagnosticar o bug do endpoint de branding (ver commit
 * "fix: corrige LazyInitializationException...").
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final String MDC_REQUEST_ID = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String requestId = valorOuNovoUuid(request.getHeader(HEADER_REQUEST_ID));
        MDC.put(MDC_REQUEST_ID, requestId);
        response.setHeader(HEADER_REQUEST_ID, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_REQUEST_ID);
        }
    }

    private static String valorOuNovoUuid(String headerRecebido) {
        return (headerRecebido != null && !headerRecebido.isBlank()) ? headerRecebido : UUID.randomUUID().toString();
    }
}
