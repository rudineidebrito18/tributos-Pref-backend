package br.com.tributos.shared.exportacao;

public record ResultadoExportacao(
    byte[] conteudo,
    String contentType,
    String nomeArquivo
) {
}
