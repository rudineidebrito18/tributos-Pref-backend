package br.com.tributos.iss.domain;

public enum TipoCatalogoIss {
    TIPO_CONTRIBUINTE("tipo-contribuinte"),
    SITUACAO_CADASTRAL("situacao-cadastral"),
    STATUS_CREDENCIAMENTO("status-credenciamento"),
    REGIME_TRIBUTARIO("regime-tributario");

    private final String pathSegment;

    TipoCatalogoIss(String pathSegment) {
        this.pathSegment = pathSegment;
    }

    public String pathSegment() {
        return pathSegment;
    }

    public static TipoCatalogoIss fromPath(String path) {
        for (TipoCatalogoIss tipo : values()) {
            if (tipo.pathSegment.equalsIgnoreCase(path) || tipo.name().equalsIgnoreCase(path)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de catálogo ISS inválido: " + path);
    }
}
