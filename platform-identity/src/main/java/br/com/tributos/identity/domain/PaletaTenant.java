package br.com.tributos.identity.domain;

/**
 * Cores de acento da identidade visual do tenant. Mesmo papel dos tokens
 * {@code --color-accent*} do frontend (ver frontend/src/lib/tenant/types.ts) — os valores
 * aqui são a fonte da verdade; o frontend só os injeta como variáveis CSS em runtime.
 */
public record PaletaTenant(
    String accent,
    String accentDark,
    String accentSecondary,
    String accentTertiary
) {

    /** Paleta neutra usada quando o tenant ainda não personalizou sua marca. */
    public static PaletaTenant padrao() {
        return new PaletaTenant("#4c8dff", "#2e6bdb", "#ff5d72", "#34d399");
    }
}
