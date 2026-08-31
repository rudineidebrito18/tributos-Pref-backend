package br.com.tributos.financeiro.domain;

public enum TipoTributacao {
    TRIBUTAVEL,
    ISENTO,
    IMUNE;

    public String descricaoLegado() {
        return switch (this) {
            case TRIBUTAVEL -> "TRIBUTÁVEL";
            case ISENTO -> "ISENTO";
            case IMUNE -> "IMUNE";
        };
    }
}
