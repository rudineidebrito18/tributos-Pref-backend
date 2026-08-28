package br.com.tributos.iptu.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CertidaoNegativaImovel(
    UUID id,
    UUID tenantId,
    UUID imovelId,
    long numero,
    LocalDate dataEmissao,
    LocalDate validade,
    String codigoVerificacao,
    Instant dataEmissaoTs
) {

    public boolean vigente(LocalDate referencia) {
        return ValidadorVigenciaDocumento.estaVigente(validade, referencia);
    }
}
