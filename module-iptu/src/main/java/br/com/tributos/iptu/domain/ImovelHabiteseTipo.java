package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ImovelHabiteseTipo(
    UUID id,
    UUID tenantId,
    String nome,
    boolean ativo,
    String titulo,
    boolean permiteDesconto,
    boolean habilitaCalculoValor,
    BigDecimal valor,
    String secretaria,
    String cargo,
    UUID assinaturaDocumentoId,
    Instant criadoEm
) {
}
