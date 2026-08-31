package br.com.tributos.kernel.cadastro;

/** Dados mínimos do contribuinte para montar cobrança PIX no BB. */
public record DadosDevedorPix(
    String nome,
    String cpf,
    String cnpj
) {
}
