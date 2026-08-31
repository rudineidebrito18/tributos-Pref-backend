package br.com.tributos.iss.adapters.in.web.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import br.com.tributos.iss.domain.TipoCertidaoIss;
import br.com.tributos.iss.domain.TributoCertidao;

public record EmitirCertidaoAvulsaRequest(
    @NotNull(message = "Informe o contribuinte.")
    UUID contribuinteId,
    @NotNull(message = "Informe o tipo da certidão.")
    TipoCertidaoIss tipo,
    LocalDate validade,
    UUID situacaoCndId,
    @NotBlank(message = "Certidão avulsa exige justificativa em observação.")
    String observacao,
    @NotEmpty(message = "Selecione ao menos um tributo.")
    List<TributoCertidao> tributos
) {
}
