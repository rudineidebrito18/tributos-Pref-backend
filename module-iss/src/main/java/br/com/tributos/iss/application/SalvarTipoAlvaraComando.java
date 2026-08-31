package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iss.domain.BaseVencimentoAlvara;

public record SalvarTipoAlvaraComando(
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
}
