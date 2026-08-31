package br.com.tributos.identity.application;

import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class AtualizarPerfilService {

    private static final Pattern POLITICA_SENHA = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,}$"
    );

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AtualizarPerfilService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario executar(UUID usuarioId, String nome, String login, String email, String password1, String password2) {
        UUID tenantId = TenantContext.getObrigatorio();
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .filter(u -> u.getTenantId().equals(tenantId))
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        validarCamposObrigatorios(nome, login, email);
        validarSenhas(password1, password2);

        String nomeNormalizado = nome.trim();
        String loginNormalizado = login.trim();
        String emailNormalizado = email.trim();

        if (usuarioRepository.existeLoginOuEmail(tenantId, loginNormalizado, emailNormalizado, usuarioId)) {
            throw new ValidationException("Login ou e-mail já está em uso neste tenant.");
        }

        usuario.atualizarPerfil(nomeNormalizado, loginNormalizado, emailNormalizado);

        if (password1 != null && !password1.isBlank()) {
            usuario.trocarSenha(passwordEncoder.encode(password1));
        }

        usuarioRepository.salvar(usuario);
        return usuario;
    }

    private static void validarCamposObrigatorios(String nome, String login, String email) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe o nome.");
        }
        if (login == null || login.isBlank()) {
            throw new ValidationException("Informe o login.");
        }
        if (email == null || email.isBlank()) {
            throw new ValidationException("Informe o e-mail.");
        }
    }

    private void validarSenhas(String password1, String password2) {
        boolean informouSenha = password1 != null && !password1.isBlank();
        boolean informouConfirmacao = password2 != null && !password2.isBlank();

        if (!informouSenha && !informouConfirmacao) {
            return;
        }
        if (!informouSenha || !informouConfirmacao) {
            throw new ValidationException("Informe a senha e a confirmação.");
        }
        if (!password1.equals(password2)) {
            throw new ValidationException("As senhas informadas não conferem.");
        }
        if (!POLITICA_SENHA.matcher(password1).matches()) {
            throw new ValidationException(
                "A senha deve ter no mínimo 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial."
            );
        }
    }
}
