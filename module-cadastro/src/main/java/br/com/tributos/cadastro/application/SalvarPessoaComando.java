package br.com.tributos.cadastro.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SalvarPessoaComando(
    String tipoPessoa,
    String cpfCnpj,
    String nome,
    String nomeFantasia,
    String razaoSocial,
    LocalDate dataNascimentoFundacao,
    String email,
    String telefone1,
    String telefone2,
    boolean ativo,
    List<EnderecoComando> enderecos
) {

    public record EnderecoComando(
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        UUID cidadeId,
        boolean principal
    ) {
    }
}
