package br.com.tributos.adapters.in.web.dto.pixbb;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Componente(BigDecimal valor) {
}
