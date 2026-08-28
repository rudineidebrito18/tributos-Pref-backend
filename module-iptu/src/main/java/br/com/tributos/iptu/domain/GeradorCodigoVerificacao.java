package br.com.tributos.iptu.domain;

import java.security.SecureRandom;
import java.util.HexFormat;

public final class GeradorCodigoVerificacao {

    private static final SecureRandom RANDOM = new SecureRandom();

    private GeradorCodigoVerificacao() {
    }

    /** Código alfanumérico de 16 bytes (32 caracteres hex) — único globalmente. */
    public static String gerar() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
