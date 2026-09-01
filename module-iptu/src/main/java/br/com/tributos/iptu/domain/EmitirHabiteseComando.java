package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EmitirHabiteseComando(
    UUID tipoId,
    LocalDate dataEmissao,
    Short ano,
    LocalDate validade,
    UUID contribuinteId,
    BigDecimal areaImovel,
    LocalDate dataConclusao,
    String numeroAlvara,
    LocalDate dataAlvara,
    LocalDate validadeAlvara,
    BigDecimal valorBaseCalculo,
    BigDecimal desconto,
    BigDecimal frente,
    BigDecimal fundos,
    BigDecimal ladoEsquerdo,
    BigDecimal ladoDireito,
    String observacao,
    List<ResponsavelComando> responsaveis
) {

    public record ResponsavelComando(
        String nome,
        String profissao,
        String documento
    ) {
    }
}
