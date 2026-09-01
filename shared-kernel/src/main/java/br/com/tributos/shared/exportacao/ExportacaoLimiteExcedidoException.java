package br.com.tributos.shared.exportacao;

public class ExportacaoLimiteExcedidoException extends RuntimeException {

    public static final int LIMITE_LINHAS = 50_000;

    public ExportacaoLimiteExcedidoException() {
        super("Exportação limitada a " + LIMITE_LINHAS
            + " linhas. Refine os filtros e tente novamente.");
    }
}
