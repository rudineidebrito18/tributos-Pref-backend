package br.com.tributos.identity.adapters.out.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import br.com.tributos.identity.application.ports.EnviadorCodigoMfaEmail;

@Component
public class SmtpEnviadorCodigoMfaEmail implements EnviadorCodigoMfaEmail {

    private final JavaMailSender mailSender;
    private final String remetente;

    public SmtpEnviadorCodigoMfaEmail(
        JavaMailSender mailSender,
        @Value("${app.security.mfa.email.remetente:noreply@tributos.local}") String remetente
    ) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Override
    public void enviar(String destinatario, String codigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject("Código de verificação — Tributos");
        mensagem.setText("""
            Seu código de verificação é: %s

            Ele expira em alguns minutos. Se você não solicitou este código, ignore este e-mail.
            """.formatted(codigo));
        mailSender.send(mensagem);
    }
}
