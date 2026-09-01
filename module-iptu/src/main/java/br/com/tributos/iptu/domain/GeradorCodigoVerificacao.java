package br.com.tributos.iptu.domain;

import java.security.SecureRandom;
import java.util.HexFormat;

public final class GeradorCodigoVerificacao {

    private static final SecureRandom RANDOM = new SecureRandom();

    private GeradorCodigoVerificacao() {
    }

    private static final String ALFANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TAMANHO_LEGADO = 20;

    /** Código alfanumérico de 16 bytes (32 caracteres hex) — único globalmente. */
    public static String gerar() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    /** Código alfanumérico de 20 caracteres — padrão do legado para habite-se. */
    public static String gerarLegado() {
        StringBuilder sb = new StringBuilder(TAMANHO_LEGADO);
        for (int i = 0; i < TAMANHO_LEGADO; i++) {
            sb.append(ALFANUM.charAt(RANDOM.nextInt(ALFANUM.length())));
        }
        return sb.toString();
    }
}
