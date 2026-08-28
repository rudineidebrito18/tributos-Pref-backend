package br.com.tributos.identity.application;

import java.util.Set;

import br.com.tributos.identity.domain.PaletaTenant;
import br.com.tributos.identity.domain.TipoEntidade;

/**
 * Entrada do onboarding administrativo de um tenant. {@code paleta} e
 * {@code dominioProprio} são opcionais (nulos = paleta padrão / sem domínio próprio ainda).
 */
public record CriarTenantComando(
    String slug,
    String nome,
    String uf,
    TipoEntidade tipoEntidade,
    String logoUrl,
    PaletaTenant paleta,
    Set<String> modulosAtivos,
    String dominioProprio,
    String loginAdminInicial,
    String emailAdminInicial
) {
}
