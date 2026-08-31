package br.com.tributos.iss.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.tributos.iss.domain.TipoCertidaoIss;
import br.com.tributos.iss.domain.TributoCertidao;

public record EmitirCertidaoComando(
    UUID contribuinteId,
    TipoCertidaoIss tipo,
    LocalDate validade,
    UUID situacaoCndId,
    String observacao,
    boolean avulsa,
    List<TributoCertidao> tributos
) {
}
