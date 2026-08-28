package br.com.tributos.kernel.events;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Publicado pelo módulo IPTU para cada parcela de lançamento — consumido pelo Financeiro.
 */
public record LancamentoIptuParcelaGeradaEvent(
    UUID parcelaId,
    UUID tenantId,
    UUID proprietarioPessoaId,
    UUID lancamentoId,
    UUID imovelId,
    int exercicio,
    int numeroParcela,
    BigDecimal valor,
    LocalDate vencimento
) {
}
