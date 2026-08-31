package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iss.domain.BaseVencimentoAlvara;
import br.com.tributos.iss.domain.TipoAlvara;

public record TipoAlvaraResponse(
    UUID id,
    String nome,
    BigDecimal valorBase,
    int diasValidade,
    boolean ativo,
    Short anoVigencia,
    String identificacaoModeloDocumento,
    boolean permiteValorDinamico,
    boolean permiteCalculoValor,
    String unidadeMedidaDescritivo,
    boolean habilitarValidade,
    boolean habilitarCalculoVencimento,
    BaseVencimentoAlvara baseVencimento,
    Integer diasMesesVencimento,
    String titulo,
    String secretaria,
    String cargo,
    UUID assinaturaDocumentoId
) {

    public static TipoAlvaraResponse de(TipoAlvara tipo) {
        return new TipoAlvaraResponse(
            tipo.id(),
            tipo.nome(),
            tipo.valorBase(),
            tipo.diasValidade(),
            tipo.ativo(),
            tipo.anoVigencia(),
            tipo.identificacaoModeloDocumento(),
            tipo.permiteValorDinamico(),
            tipo.permiteCalculoValor(),
            tipo.unidadeMedidaDescritivo(),
            tipo.habilitarValidade(),
            tipo.habilitarCalculoVencimento(),
            tipo.baseVencimento(),
            tipo.diasMesesVencimento(),
            tipo.titulo(),
            tipo.secretaria(),
            tipo.cargo(),
            tipo.assinaturaDocumentoId()
        );
    }
}
