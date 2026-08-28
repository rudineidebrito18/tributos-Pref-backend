package br.com.tributos.iptu.domain;

public enum TipoCatalogoIptu {
    TIPO_IMOVEL("tipo-imovel"),
    TIPO_EDIFICACAO("tipo-edificacao"),
    DESTINACAO("destinacao"),
    TIPO_LIMITACAO("tipo-limitacao"),
    HABITESE_TIPO("habitese-tipo");

    private final String pathSegment;

    TipoCatalogoIptu(String pathSegment) {
        this.pathSegment = pathSegment;
    }

    public String pathSegment() {
        return pathSegment;
    }

    public static TipoCatalogoIptu fromPath(String path) {
        for (TipoCatalogoIptu tipo : values()) {
            if (tipo.pathSegment.equalsIgnoreCase(path) || tipo.name().equalsIgnoreCase(path)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de catálogo IPTU inválido: " + path);
    }
}
