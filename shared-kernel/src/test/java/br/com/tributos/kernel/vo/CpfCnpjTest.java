package br.com.tributos.kernel.vo;

import org.junit.jupiter.api.Test;

import br.com.tributos.kernel.exception.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CpfCnpjTest {

    @Test
    void deveAceitarCpfValidoComPontuacao() {
        CpfCnpj doc = CpfCnpj.de("529.982.247-25");
        assertThat(doc.tipo()).isEqualTo(CpfCnpj.Tipo.CPF);
        assertThat(doc.apenasDigitos()).isEqualTo("52998224725");
    }

    @Test
    void deveRejeitarCpfInvalido() {
        assertThatThrownBy(() -> CpfCnpj.de("111.111.111-11"))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("CPF inválido");
    }

    @Test
    void deveAceitarCnpjValido() {
        CpfCnpj doc = CpfCnpj.de("11.444.777/0001-61");
        assertThat(doc.tipo()).isEqualTo(CpfCnpj.Tipo.CNPJ);
        assertThat(doc.apenasDigitos()).isEqualTo("11444777000161");
    }

    @Test
    void deveRejeitarCnpjInvalido() {
        assertThatThrownBy(() -> CpfCnpj.de("00.000.000/0000-00"))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("CNPJ inválido");
    }
}
