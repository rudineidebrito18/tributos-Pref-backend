package br.com.tributos.kernel.iptu;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Porta para consulta e transferência de imóveis — implementada pelo módulo IPTU.
 */
public interface ImovelItbiPort {

    ImovelItbiDados buscarDados(UUID imovelId);

    void transferirTitularidade(UUID imovelId, UUID novoProprietarioId);

    void transferirTitularidadePorPartes(
        UUID imovelId,
        List<ParteTransferencia> transmitentes,
        List<ParteTransferencia> adquirentes
    );

    record ImovelItbiDados(UUID id, UUID proprietarioId, BigDecimal valorVenalReferencia, boolean ativo) {
    }

    record ParteTransferencia(UUID contribuinteId, BigDecimal porcentagem, boolean principal) {
    }
}
