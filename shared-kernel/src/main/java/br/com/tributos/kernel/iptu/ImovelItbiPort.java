package br.com.tributos.kernel.iptu;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Porta para consulta e transferência de imóveis — implementada pelo módulo IPTU.
 */
public interface ImovelItbiPort {

    ImovelItbiDados buscarDados(UUID imovelId);

    void transferirTitularidade(UUID imovelId, UUID novoProprietarioId);

    record ImovelItbiDados(UUID id, UUID proprietarioId, BigDecimal valorVenalReferencia, boolean ativo) {
    }
}
