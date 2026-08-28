package br.com.tributos.kernel.audit;

public record RegistroAuditoria(
    String entidade,
    String entidadeId,
    String acao,
    Object dadosAntes,
    Object dadosDepois
) {
}
