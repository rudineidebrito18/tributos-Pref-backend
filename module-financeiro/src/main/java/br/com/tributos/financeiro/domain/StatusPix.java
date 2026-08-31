package br.com.tributos.financeiro.domain;

public enum StatusPix {
    ATIVA,
    CONCLUIDA,
    EM_PROCESSAMENTO,
    NAO_REALIZADO,
    DEVOLVIDO,
    REMOVIDA_PELO_USUARIO_RECEBEDOR,
    REMOVIDA_PELO_PSP,
    ATUALIZACAO_MANUAL;

    public String descricaoLegado() {
        if (this == ATUALIZACAO_MANUAL) {
            return "ATUALIZAÇÃO MANUAL";
        }
        return name();
    }
}
