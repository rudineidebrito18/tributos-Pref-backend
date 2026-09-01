package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.tributos.iptu.domain.HabiteseImovel;
import br.com.tributos.iptu.domain.HabiteseResponsavel;
import br.com.tributos.iptu.domain.SituacaoFiscalHabitese;

public record HabiteseImovelResponse(
    UUID id,
    UUID imovelId,
    UUID tipoId,
    long numero,
    LocalDate dataEmissao,
    Instant dataEmissaoTs,
    Short ano,
    LocalDate validade,
    UUID contribuinteId,
    BigDecimal areaImovel,
    LocalDate dataConclusao,
    String numeroAlvara,
    LocalDate dataAlvara,
    LocalDate validadeAlvara,
    BigDecimal valorBaseCalculo,
    BigDecimal baseCalculo,
    BigDecimal desconto,
    BigDecimal valor,
    BigDecimal frente,
    BigDecimal fundos,
    BigDecimal ladoEsquerdo,
    BigDecimal ladoDireito,
    String observacao,
    String codigoVerificacao,
    SituacaoFiscalHabitese situacaoFiscal,
    List<HabiteseResponsavelResponse> responsaveis
) {

    public static HabiteseImovelResponse de(HabiteseImovel habitese) {
        return new HabiteseImovelResponse(
            habitese.id(),
            habitese.imovelId(),
            habitese.tipoId(),
            habitese.numero(),
            habitese.dataEmissao(),
            habitese.dataEmissaoTs(),
            habitese.ano(),
            habitese.validade(),
            habitese.contribuinteId(),
            habitese.areaImovel(),
            habitese.dataConclusao(),
            habitese.numeroAlvara(),
            habitese.dataAlvara(),
            habitese.validadeAlvara(),
            habitese.valorBaseCalculo(),
            habitese.baseCalculo(),
            habitese.desconto(),
            habitese.valor(),
            habitese.frente(),
            habitese.fundos(),
            habitese.ladoEsquerdo(),
            habitese.ladoDireito(),
            habitese.observacao(),
            habitese.codigoVerificacao(),
            habitese.situacaoFiscal(),
            habitese.responsaveis() != null
                ? habitese.responsaveis().stream().map(HabiteseResponsavelResponse::de).toList()
                : List.of()
        );
    }
}
