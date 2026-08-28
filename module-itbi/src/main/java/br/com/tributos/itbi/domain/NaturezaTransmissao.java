package br.com.tributos.itbi.domain;

import java.util.UUID;

public record NaturezaTransmissao(UUID id, UUID tenantId, String nome, boolean ativo) {
}
