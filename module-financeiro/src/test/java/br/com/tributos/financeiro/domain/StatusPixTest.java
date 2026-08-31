package br.com.tributos.financeiro.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusPixTest {

    @Test
    void deveMapearDescricaoLegado() {
        assertThat(StatusPix.CONCLUIDA.descricaoLegado()).isEqualTo("CONCLUIDA");
        assertThat(StatusPix.ATUALIZACAO_MANUAL.descricaoLegado()).isEqualTo("ATUALIZAÇÃO MANUAL");
        assertThat(StatusPix.ATIVA.descricaoLegado()).isEqualTo("ATIVA");
        assertThat(StatusPix.EM_PROCESSAMENTO.descricaoLegado()).isEqualTo("EM_PROCESSAMENTO");
        assertThat(StatusPix.NAO_REALIZADO.descricaoLegado()).isEqualTo("NAO_REALIZADO");
        assertThat(StatusPix.DEVOLVIDO.descricaoLegado()).isEqualTo("DEVOLVIDO");
        assertThat(StatusPix.REMOVIDA_PELO_USUARIO_RECEBEDOR.descricaoLegado()).isEqualTo("REMOVIDA_PELO_USUARIO_RECEBEDOR");
        assertThat(StatusPix.REMOVIDA_PELO_PSP.descricaoLegado()).isEqualTo("REMOVIDA_PELO_PSP");
    }
}
