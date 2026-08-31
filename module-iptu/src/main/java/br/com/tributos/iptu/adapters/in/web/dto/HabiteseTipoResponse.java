package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.tributos.iptu.domain.ImovelHabiteseTipo;

public record HabiteseTipoResponse(
    UUID id,
    String nome,
    boolean ativo,
    String titulo,
    boolean permiteDesconto,
    boolean habilitaCalculoValor,
    BigDecimal valor,
    String secretaria,
    String cargo,
    UUID assinaturaDocumentoId,
    Instant criadoEm
) {
    public static HabiteseTipoResponse de(ImovelHabiteseTipo tipo) {
        return new HabiteseTipoResponse(
            tipo.id(),
            tipo.nome(),
            tipo.ativo(),
            tipo.titulo(),
            tipo.permiteDesconto(),
            tipo.habilitaCalculoValor(),
            tipo.valor(),
            tipo.secretaria(),
            tipo.cargo(),
            tipo.assinaturaDocumentoId(),
            tipo.criadoEm()
        );
    }
}
