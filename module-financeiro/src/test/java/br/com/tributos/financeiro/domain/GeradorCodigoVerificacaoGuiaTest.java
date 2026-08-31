package br.com.tributos.financeiro.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class GeradorCodigoVerificacaoGuiaTest {

    @Test
    void deveGerarCodigoAlfanumericoDe20Caracteres() {
        String codigo = GeradorCodigoVerificacaoGuia.gerar();
        assertThat(codigo).hasSize(20);
        assertThat(codigo).matches("[A-Z0-9]{20}");
    }

    @Test
    void deveGerarCodigosDistintos() {
        Set<String> gerados = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            gerados.add(GeradorCodigoVerificacaoGuia.gerar());
        }
        assertThat(gerados).hasSizeGreaterThan(45);
    }
}
