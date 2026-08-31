package br.com.tributos.shared.cripto;

/**
 * Cifra simétrica para segredos em repouso (credenciais PIX, senhas de certificado, tokens).
 */
public interface CifradorSegredo {

    String cifrar(String textoClaro);

    String decifrar(String textoCifrado);
}
