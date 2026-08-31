package br.com.tributos.kernel.identity;

import java.util.UUID;

/**
 * Porta para criação de usuário de acesso vinculado a contribuinte — implementada pelo
 * módulo platform-identity.
 */
public interface UsuarioAcessoPort {

    record ResultadoCriacaoUsuario(UUID usuarioId, String login, String emailNotificacao) {
    }

    ResultadoCriacaoUsuario criarComSenhaGerada(UUID tenantId, String login, String email, String nome);
}
