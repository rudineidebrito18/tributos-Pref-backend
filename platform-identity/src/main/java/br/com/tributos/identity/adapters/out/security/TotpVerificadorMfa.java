package br.com.tributos.identity.adapters.out.security;

import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.tributos.identity.application.ports.VerificadorMfa;

/**
 * TOTP (RFC 6238) sobre HMAC-SHA1 — o mesmo algoritmo do Google Authenticator, Authy,
 * 1Password etc. Implementado sem biblioteca de OTP dedicada (só {@code commons-codec}
 * para Base32, já gerenciado pelo BOM do Spring Boot): o algoritmo é curto, estável desde
 * 2011 e evita mais uma dependência externa para um cálculo de ~10 linhas.
 */
@Component
public class TotpVerificadorMfa implements VerificadorMfa {

    private static final int TAMANHO_SEGREDO_BYTES = 20; // 160 bits — recomendação da RFC para HMAC-SHA1
    private static final int DIGITOS = 6;
    private static final int PERIODO_SEGUNDOS = 30;
    private static final int JANELA_TOLERANCIA_PASSOS = 1; // aceita 1 passo (±30s) de deriva de relógio

    private final SecureRandom secureRandom = new SecureRandom();
    private final String nomeEmissor;

    public TotpVerificadorMfa(@Value("${app.security.mfa.emissor:Tributos}") String nomeEmissor) {
        this.nomeEmissor = nomeEmissor;
    }

    @Override
    public String gerarSegredo() {
        byte[] bytes = new byte[TAMANHO_SEGREDO_BYTES];
        secureRandom.nextBytes(bytes);
        return new Base32().encodeToString(bytes).replace("=", "");
    }

    @Override
    public String gerarUriProvisionamento(String segredo, String identificadorUsuario) {
        String label = URLEncoder.encode(nomeEmissor + ":" + identificadorUsuario, StandardCharsets.UTF_8);
        String issuer = URLEncoder.encode(nomeEmissor, StandardCharsets.UTF_8);
        return "otpauth://totp/%s?secret=%s&issuer=%s&algorithm=SHA1&digits=%d&period=%d"
            .formatted(label, segredo, issuer, DIGITOS, PERIODO_SEGUNDOS);
    }

    @Override
    public boolean validarCodigo(String segredo, String codigo) {
        if (codigo == null || !codigo.matches("\\d{" + DIGITOS + "}")) {
            return false;
        }
        long contadorAtual = Instant.now().getEpochSecond() / PERIODO_SEGUNDOS;
        // Janela de tolerância: aceita o passo anterior/seguinte para absorver pequena
        // deriva de relógio entre o celular do usuário e o servidor.
        for (int deslocamento = -JANELA_TOLERANCIA_PASSOS; deslocamento <= JANELA_TOLERANCIA_PASSOS; deslocamento++) {
            if (codigo.equals(gerarCodigo(segredo, contadorAtual + deslocamento))) {
                return true;
            }
        }
        return false;
    }

    // Visibilidade de pacote (não private) só para permitir teste determinístico com um
    // contador fixo, sem precisar mockar relógio — não é API pública do adapter.
    String gerarCodigo(String segredoBase32, long contador) {
        byte[] chave = new Base32().decode(segredoBase32);
        byte[] dadosContador = ByteBuffer.allocate(8).putLong(contador).array();

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(chave, "HmacSHA1"));
            byte[] hash = mac.doFinal(dadosContador);

            int deslocamento = hash[hash.length - 1] & 0x0F;
            int binario = ((hash[deslocamento] & 0x7F) << 24)
                | ((hash[deslocamento + 1] & 0xFF) << 16)
                | ((hash[deslocamento + 2] & 0xFF) << 8)
                | (hash[deslocamento + 3] & 0xFF);

            int codigo = binario % (int) Math.pow(10, DIGITOS);
            return String.format("%0" + DIGITOS + "d", codigo);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Falha ao calcular código TOTP", ex);
        }
    }
}
