package br.com.tributos.financeiro.domain;

import java.security.SecureRandom;

public final class GeradorCodigoVerificacaoGuia {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALFANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TAMANHO = 20;

    private GeradorCodigoVerificacaoGuia() {
    }

    public static String gerar() {
        StringBuilder sb = new StringBuilder(TAMANHO);
        for (int i = 0; i < TAMANHO; i++) {
            sb.append(ALFANUM.charAt(RANDOM.nextInt(ALFANUM.length())));
        }
        return sb.toString();
    }
}
