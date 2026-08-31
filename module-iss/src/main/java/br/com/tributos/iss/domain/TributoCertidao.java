package br.com.tributos.iss.domain;

public enum TributoCertidao {
    ALVARA("ALVARÁ - TAXA DE FISCALIZAÇÃO - ALVARÁ"),
    HABITE_SE("HABITE-SE - HABITE-SE"),
    IPTU("IPTU - IMPOSTO PREDIAL E TERRITORIAL URBANO - IPTU"),
    IRPF("IMPOSTO SOBRE A RENDA DAS PESSOAS FÍSICAS - IRPF"),
    IRPJ("IMPOSTO SOBRE A RENDA DAS PESSOAS JURÍDICA - IRPJ"),
    ISS("ISS - IMPOSTO SOBRE SERVIÇOS - ISS"),
    ITBI("ITBI - IMPOSTO SOBRE TRANSMISSÃO DE BENS IMÓVEIS - ITBI");

    private final String descricao;

    TributoCertidao(String descricao) {
        this.descricao = descricao;
    }

    public String descricao() {
        return descricao;
    }
}
