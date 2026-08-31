package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SalvarContribuinteRequest(
    @NotNull(message = "Informe a pessoa vinculada.")
    UUID pessoaId,

    @NotBlank(message = "Informe a inscrição municipal.")
    String inscricaoMunicipal,

    @NotNull(message = "Informe o tipo de contribuinte.")
    UUID tipoContribuinteId,

    @NotNull(message = "Informe a situação cadastral.")
    UUID situacaoCadastralId,

    @NotNull(message = "Informe o regime tributário.")
    UUID regimeTributarioId,

    String nomeFantasia,

    String inscricaoEstadual,

    String contato,

    String telefone2,

    @Email(message = "E-mail para recebimento de notas inválido.")
    String emailNota,

    UUID usuarioId,

    String nomeContador,

    @Email(message = "E-mail do contador inválido.")
    String emailContador
) {
}
