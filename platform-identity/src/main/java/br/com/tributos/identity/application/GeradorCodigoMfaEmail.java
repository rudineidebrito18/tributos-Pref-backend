package br.com.tributos.identity.application;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.tributos.identity.application.ports.EnviadorCodigoMfaEmail;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.kernel.exception.ValidationException;

@Component
public class GeradorCodigoMfaEmail {

    private static final int DIGITOS = 6;

    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder passwordEncoder;
    private final EnviadorCodigoMfaEmail enviadorCodigoMfaEmail;
    private final Duration validade;

    public GeradorCodigoMfaEmail(
        PasswordEncoder passwordEncoder,
        EnviadorCodigoMfaEmail enviadorCodigoMfaEmail,
        @Value("${app.security.mfa.email.expiracao-minutos:10}") int expiracaoMinutos
    ) {
        this.passwordEncoder = passwordEncoder;
        this.enviadorCodigoMfaEmail = enviadorCodigoMfaEmail;
        this.validade = Duration.ofMinutes(expiracaoMinutos);
    }

    public String gerarCodigo() {
        int valor = secureRandom.nextInt(1_000_000);
        return String.format("%0" + DIGITOS + "d", valor);
    }

    public void registrarDesafio(Usuario usuario, String codigo) {
        validarEmailCadastrado(usuario);
        String hash = passwordEncoder.encode(codigo);
        usuario.registrarDesafioEmail(hash, Instant.now().plus(validade));
        enviadorCodigoMfaEmail.enviar(usuario.getEmail(), codigo);
    }

    public void registrarHabilitacao(Usuario usuario, String codigo) {
        validarEmailCadastrado(usuario);
        String hash = passwordEncoder.encode(codigo);
        usuario.iniciarHabilitacaoMfaEmail(hash, Instant.now().plus(validade));
        enviadorCodigoMfaEmail.enviar(usuario.getEmail(), codigo);
    }

    public String mensagemEnvio(String email) {
        return "Código enviado para " + mascararEmail(email) + ".";
    }

    private static void validarEmailCadastrado(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new ValidationException("Cadastre um e-mail válido no perfil antes de habilitar MFA por e-mail.");
        }
    }

    private static String mascararEmail(String email) {
        int arroba = email.indexOf('@');
        if (arroba <= 1) {
            return "***" + email.substring(Math.max(0, arroba));
        }
        return email.charAt(0) + "***" + email.substring(arroba);
    }
}
