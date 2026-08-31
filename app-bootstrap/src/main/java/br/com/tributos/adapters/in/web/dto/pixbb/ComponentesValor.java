package br.com.tributos.adapters.in.web.dto.pixbb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ComponentesValor(Componente original) {
}
