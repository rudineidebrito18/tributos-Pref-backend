package br.com.tributos.identity.application;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.tributos.identity.application.ports.VerificadorMfa;
import br.com.tributos.identity.domain.TipoMfa;
import br.com.tributos.identity.domain.Usuario;

@Component
public class ValidadorMfaPorTipo {

    private final VerificadorMfa verificadorTotp;
    private final PasswordEncoder passwordEncoder;

    public ValidadorMfaPorTipo(VerificadorMfa verificadorTotp, PasswordEncoder passwordEncoder) {
        this.verificadorTotp = verificadorTotp;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean validar(Usuario usuario, String codigo) {
        if (usuario.getMfaTipo() == TipoMfa.TOTP) {
            return verificadorTotp.validarCodigo(usuario.getMfaSecret(), codigo);
        }
        if (usuario.getMfaTipo() == TipoMfa.EMAIL) {
            return validarCodigoEmail(usuario, codigo);
        }
        return false;
    }

    private boolean validarCodigoEmail(Usuario usuario, String codigo) {
        if (codigo == null || !codigo.matches("\\d{6}")) {
            return false;
        }
        if (usuario.getMfaSecret() == null || usuario.codigoEmailExpirado(Instant.now())) {
            return false;
        }
        return passwordEncoder.matches(codigo, usuario.getMfaSecret());
    }
}
