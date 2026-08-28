package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iss.domain.Servico;

public record ServicoResponse(
    UUID id,
    String codigoLc116,
    String descricao,
    BigDecimal aliquotaMinima,
    BigDecimal aliquotaMaxima,
    boolean ativo
) {

    public static ServicoResponse de(Servico servico) {
        return new ServicoResponse(
            servico.id(),
            servico.codigoLc116(),
            servico.descricao(),
            servico.aliquotaMinima(),
            servico.aliquotaMaxima(),
            servico.ativo()
        );
    }
}
