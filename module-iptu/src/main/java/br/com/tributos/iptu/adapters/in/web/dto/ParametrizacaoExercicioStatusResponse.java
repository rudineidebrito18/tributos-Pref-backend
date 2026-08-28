package br.com.tributos.iptu.adapters.in.web.dto;

import java.util.List;
import java.util.UUID;

import br.com.tributos.iptu.domain.ParametrizacaoExercicioStatus;
import br.com.tributos.iptu.domain.ParametrizacaoExercicioStatus.CombinacaoAliquotaFaltante;

public record ParametrizacaoExercicioStatusResponse(
    int exercicio,
    boolean zonasOk,
    boolean valoresTerrenoOk,
    boolean aliquotasOk,
    long imoveisSemZona,
    List<CombinacaoAliquotaFaltanteResponse> combinacoesFaltantes,
    boolean completo
) {

    public record CombinacaoAliquotaFaltanteResponse(
        UUID destinacaoId,
        UUID zonaFiscalId
    ) {
    }

    public static ParametrizacaoExercicioStatusResponse de(ParametrizacaoExercicioStatus status) {
        return new ParametrizacaoExercicioStatusResponse(
            status.exercicio(),
            status.zonasOk(),
            status.valoresTerrenoOk(),
            status.aliquotasOk(),
            status.imoveisSemZona(),
            status.combinacoesFaltantes().stream().map(ParametrizacaoExercicioStatusResponse::deCombinacao).toList(),
            status.completo()
        );
    }

    private static CombinacaoAliquotaFaltanteResponse deCombinacao(CombinacaoAliquotaFaltante combinacao) {
        return new CombinacaoAliquotaFaltanteResponse(combinacao.destinacaoId(), combinacao.zonaFiscalId());
    }
}
