package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.UUID;

public record ParametrizacaoExercicioStatus(
    int exercicio,
    boolean zonasOk,
    boolean valoresTerrenoOk,
    boolean aliquotasOk,
    long imoveisSemZona,
    List<CombinacaoAliquotaFaltante> combinacoesFaltantes,
    boolean completo
) {

    public record CombinacaoAliquotaFaltante(
        UUID destinacaoId,
        UUID zonaFiscalId
    ) {
    }
}
