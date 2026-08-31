package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record Servico(
    UUID id,
    UUID tenantId,
    String codigoLc116,
    String descricao,
    BigDecimal aliquotaMinima,
    BigDecimal aliquotaMaxima,
    boolean ativo,
    UUID grupoServicoId,
    String codigoNbs,
    String codigoTributacaoNacional,
    String indop,
    String cClassTrib
) {
}
