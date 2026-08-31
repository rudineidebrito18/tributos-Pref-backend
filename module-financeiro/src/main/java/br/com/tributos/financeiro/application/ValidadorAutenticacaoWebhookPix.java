package br.com.tributos.financeiro.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.kernel.pixbb.ConfiguracaoPixBbPort;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixOperacional;

@Service
public class ValidadorAutenticacaoWebhookPix {

    private static final String CABECALHO_CERTIFICADO = "SUCCESS";

    private final ConfiguracaoPixBbPort configuracaoPixBbPort;

    public ValidadorAutenticacaoWebhookPix(ConfiguracaoPixBbPort configuracaoPixBbPort) {
        this.configuracaoPixBbPort = configuracaoPixBbPort;
    }

    public boolean validar(
        UUID tenantId,
        String certificadoVerificado,
        String tokenCabecalho,
        String authorization
    ) {
        if (certificadoVerificado != null && CABECALHO_CERTIFICADO.equalsIgnoreCase(certificadoVerificado.trim())) {
            return true;
        }
        Optional<ConfiguracaoPixOperacional> config = configuracaoPixBbPort.buscarAtiva(tenantId);
        String esperado = config.map(ConfiguracaoPixOperacional::webhookToken).orElse(null);
        if (esperado == null || esperado.isBlank()) {
            return false;
        }
        String recebido = extrairToken(tokenCabecalho, authorization);
        if (recebido == null || recebido.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
            esperado.getBytes(StandardCharsets.UTF_8),
            recebido.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String extrairToken(String tokenCabecalho, String authorization) {
        if (tokenCabecalho != null && !tokenCabecalho.isBlank()) {
            return tokenCabecalho.trim();
        }
        if (authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authorization.substring(7).trim();
        }
        return null;
    }
}
