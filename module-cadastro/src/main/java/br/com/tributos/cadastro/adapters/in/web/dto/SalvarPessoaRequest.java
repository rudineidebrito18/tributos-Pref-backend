package br.com.tributos.cadastro.adapters.in.web.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SalvarPessoaRequest(
    @NotBlank(message = "Informe o tipo (PF ou PJ).")
    @Pattern(regexp = "PF|PJ", message = "Tipo deve ser PF ou PJ.")
    String tipoPessoa,

    @NotBlank(message = "Informe o CPF ou CNPJ.")
    String cpfCnpj,

    @NotBlank(message = "Informe o nome.")
    String nome,

    String nomeFantasia,
    String razaoSocial,
    LocalDate dataNascimentoFundacao,

    @Email(message = "E-mail inválido.")
    String email,

    String telefone1,
    String telefone2,
    boolean ativo,

    @Valid
    List<EnderecoRequest> enderecos
) {

    public record EnderecoRequest(
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
