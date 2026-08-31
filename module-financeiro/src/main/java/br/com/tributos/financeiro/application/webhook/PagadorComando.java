package br.com.tributos.financeiro.application.webhook;

public record PagadorComando(String cpf, String cnpj, String nome) {
}
