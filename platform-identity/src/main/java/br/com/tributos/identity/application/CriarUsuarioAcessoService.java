package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.identity.domain.TipoMfa;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.identity.UsuarioAcessoPort;

@Service
public class CriarUsuarioAcessoService implements UsuarioAcessoPort {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CriarUsuarioAcessoService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ResultadoCriacaoUsuario criarComSenhaGerada(UUID tenantId, String login, String email, String nome) {
        String loginNormalizado = login.trim();
        String emailNormalizado = email.trim();

        if (usuarioRepository.existeLoginOuEmail(tenantId, loginNormalizado, emailNormalizado, null)) {
            throw new ValidationException("Login ou e-mail já utilizado neste tenant.");
        }

        UUID usuarioId = UUID.randomUUID();
        usuarioRepository.salvar(new Usuario(
            usuarioId,
            tenantId,
            nome != null ? nome.trim() : loginNormalizado,
            loginNormalizado,
            emailNormalizado,
            null,
            passwordEncoder.encode(SenhaTemporariaFactory.gerar()),
            false,
            TipoMfa.NENHUM,
            null,
            true
        ));

        return new ResultadoCriacaoUsuario(usuarioId, loginNormalizado, emailNormalizado);
    }
}
