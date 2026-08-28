package br.com.tributos.iss.adapters.in.web.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.iss.domain.CertidaoIss;
import br.com.tributos.iss.domain.TipoCertidaoIss;

public record CertidaoIssResponse(
    UUID id,
    TipoCertidaoIss tipo,
    UUID contribuinteId,
    long numero,
    String codigoVerificacao,
    Instant dataEmissao,
    LocalDate validade
) {

    public static CertidaoIssResponse de(CertidaoIss certidao) {
        return new CertidaoIssResponse(
            certidao.id(),
            certidao.tipo(),
            certidao.contribuinteId(),
            certidao.numero(),
            certidao.codigoVerificacao(),
            certidao.dataEmissao(),
            certidao.validade()
        );
    }
}
