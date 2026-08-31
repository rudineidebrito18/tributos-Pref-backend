package br.com.tributos.financeiro.adapters.out.pixbb;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import br.com.tributos.financeiro.domain.StatusPix;

class MapeadorStatusPixBbTest {

    @Test
    void deveMapearStatusConhecidos() {
        assertThat(MapeadorStatusPixBb.mapear("CONCLUIDA")).contains(StatusPix.CONCLUIDA);
        assertThat(MapeadorStatusPixBb.mapear("ATIVA")).contains(StatusPix.ATIVA);
    }

    @Test
    void deveRetornarVazioParaStatusDesconhecido() {
        Optional<StatusPix> resultado = MapeadorStatusPixBb.mapear("STATUS_INVENTADO");
        assertThat(resultado).isEmpty();
    }
}
