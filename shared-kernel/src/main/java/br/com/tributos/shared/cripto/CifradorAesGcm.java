package br.com.tributos.shared.cripto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-256-GCM com IV aleatório de 12 bytes prefixado ao ciphertext (Base64).
 */
public final class CifradorAesGcm implements CifradorSegredo {

    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;

    private final SecretKeySpec chave;

    public CifradorAesGcm(String segredo) {
        if (segredo == null || segredo.isBlank()) {
            throw new IllegalArgumentException("APP_SECRET_KEY não pode ser vazia.");
        }
        this.chave = new SecretKeySpec(derivarChave256(segredo), "AES");
    }

    @Override
    public String cifrar(String textoClaro) {
        if (textoClaro == null) {
            throw new IllegalArgumentException("Texto a cifrar não pode ser nulo.");
        }
        try {
            byte[] iv = new byte[IV_BYTES];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, chave, new GCMParameterSpec(TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(textoClaro.getBytes(StandardCharsets.UTF_8));

            byte[] combinado = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combinado, 0, iv.length);
            System.arraycopy(ciphertext, 0, combinado, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(combinado);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao cifrar segredo.", ex);
        }
    }

    @Override
    public String decifrar(String textoCifrado) {
        if (textoCifrado == null || textoCifrado.isBlank()) {
            throw new IllegalArgumentException("Texto cifrado inválido.");
        }
        try {
            byte[] combinado = Base64.getDecoder().decode(textoCifrado);
            if (combinado.length <= IV_BYTES) {
                throw new IllegalArgumentException("Payload cifrado inválido.");
            }
            byte[] iv = Arrays.copyOfRange(combinado, 0, IV_BYTES);
            byte[] ciphertext = Arrays.copyOfRange(combinado, IV_BYTES, combinado.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, chave, new GCMParameterSpec(TAG_BITS, iv));
            byte[] plain = cipher.doFinal(ciphertext);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao decifrar segredo.", ex);
        }
    }

    private static byte[] derivarChave256(String segredo) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(segredo.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 indisponível.", ex);
        }
    }
}
