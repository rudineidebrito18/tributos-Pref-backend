package br.com.tributos.financeiro.domain;

import java.util.UUID;

public record FormaPagamento(UUID id, String codigo, String nome) {
}
