package br.com.tributos;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import br.com.tributos.adapters.in.web.dto.pixbb.PixRecebido;
import br.com.tributos.adapters.in.web.dto.pixbb.WebhookPixPayload;
import br.com.tributos.support.AbstractIntegrationTest;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookPixPayloadDeserializationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveDesserializarPayloadOficialDaDocumentacao() throws Exception {
        byte[] json = new ClassPathResource("fixtures/pixbb/webhook-pix-liquidacao.json")
            .getInputStream()
            .readAllBytes();

        WebhookPixPayload payload = objectMapper.readValue(json, WebhookPixPayload.class);

        assertThat(payload.pix()).hasSize(1);
        PixRecebido item = payload.pix().get(0);
        assertThat(item.valor()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(item.endToEndId()).isEqualTo("E60746948202103082223A7540Db1234");
    }

    @Test
    void deveDesserializarLoteComCamposExtrasSemFalhar() throws Exception {
        byte[] json = new ClassPathResource("fixtures/pixbb/webhook-pix-lote-pj-campos-extras.json")
            .getInputStream()
            .readAllBytes();

        WebhookPixPayload payload = objectMapper.readValue(json, WebhookPixPayload.class);

        assertThat(payload.pix()).hasSize(2);
        assertThat(payload.pix().get(0).pagador().cnpj()).isEqualTo("99328834000198");
        assertThat(payload.pix().get(1).horario()).isNotNull();
    }
}
