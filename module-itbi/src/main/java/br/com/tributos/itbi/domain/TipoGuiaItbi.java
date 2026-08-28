package br.com.tributos.itbi.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record TipoGuiaItbi(UUID id, UUID tenantId, String nome, BigDecimal aliquota, boolean ativo) {
}
