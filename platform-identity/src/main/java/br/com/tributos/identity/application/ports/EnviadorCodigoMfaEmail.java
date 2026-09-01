package br.com.tributos.identity.application.ports;

/** Porta de saída para envio do código de verificação MFA por e-mail. */
public interface EnviadorCodigoMfaEmail {

    void enviar(String destinatario, String codigo);
}
