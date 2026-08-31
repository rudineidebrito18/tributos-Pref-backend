package br.com.tributos.shared.cripto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CifradorAesGcmTest {

    private final CifradorAesGcm cifrador = new CifradorAesGcm("chave-teste-desenvolvimento-local-32b");

    @Test
    void deveCifrarEDecifrar() {
        assertThat(cifrador.decifrar(cifrador.cifrar("abc"))).isEqualTo("abc");
    }

    @Test
    void deveUsarIvAleatorio() {
        String c1 = cifrador.cifrar("abc");
        String c2 = cifrador.cifrar("abc");
        assertThat(c1).isNotEqualTo(c2);
    }
}
