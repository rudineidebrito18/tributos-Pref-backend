package br.com.tributos.adapters.in.web.dto.pixbb;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookPixPayload(List<PixRecebido> pix) {
}
