package br.com.tributos.identity.adapters.out.security;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TotpVerificadorMfaTest {

    private final TotpVerificadorMfa totp = new TotpVerificadorMfa("Tributos");

    @Test
    void deveValidarCodigoGeradoParaOMesmoSegredoNoPassoAtual() {
        String segredo = totp.gerarSegredo();
        long contadorAtual = Instant.now().getEpochSecond() / 30;
        String codigo = totp.gerarCodigo(segredo, contadorAtual);

        assertThat(codigo).matches("\\d{6}");
        assertThat(totp.validarCodigo(segredo, codigo)).isTrue();
    }

    @Test
    void doisSegredosDiferentesDevemProduzirCodigosDiferentesParaOMesmoPasso() {
        String segredoA = totp.gerarSegredo();
        String segredoB = totp.gerarSegredo();
        long contador = 100L;

        assertThat(totp.gerarCodigo(segredoA, contador)).isNotEqualTo(totp.gerarCodigo(segredoB, contador));
    }

    @Test
    void naoDeveValidarCodigoComFormatoInvalido() {
        String segredo = totp.gerarSegredo();

        assertThat(totp.validarCodigo(segredo, "abc")).isFalse();
        assertThat(totp.validarCodigo(segredo, null)).isFalse();
    }

    @Test
    void uriDeProvisionamentoDeveConterEmissorELabelCodificados() {
        String uri = totp.gerarUriProvisionamento("SEGREDOBASE32", "usuario@demo.gov.br");

        assertThat(uri).startsWith("otpauth://totp/");
        assertThat(uri).contains("secret=SEGREDOBASE32");
        assertThat(uri).contains("issuer=Tributos");
    }
}
