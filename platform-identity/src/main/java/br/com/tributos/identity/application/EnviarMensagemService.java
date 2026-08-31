package br.com.tributos.identity.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.adapters.out.security.UsuarioAutenticadoResolver;
import br.com.tributos.identity.domain.MensagemInterna;
import br.com.tributos.identity.domain.MensagemInternaDestinatario;
import br.com.tributos.identity.domain.MensagemInternaRepository;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class EnviarMensagemService {

    private final MensagemInternaRepository mensagemInternaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAutenticadoResolver usuarioAutenticadoResolver;

    public EnviarMensagemService(
        MensagemInternaRepository mensagemInternaRepository,
        UsuarioRepository usuarioRepository,
        UsuarioAutenticadoResolver usuarioAutenticadoResolver
    ) {
        this.mensagemInternaRepository = mensagemInternaRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioAutenticadoResolver = usuarioAutenticadoResolver;
    }

    public MensagemInterna executar(String assunto, String corpo, List<UUID> destinatarioIds) {
        UUID remetenteId = usuarioAtualObrigatorio();
        UUID tenantId = TenantContext.getObrigatorio();

        validarCampos(assunto, corpo, destinatarioIds);
        validarDestinatarios(tenantId, destinatarioIds);

        MensagemInterna mensagem = new MensagemInterna();
        mensagem.setId(UUID.randomUUID());
        mensagem.setTenantId(tenantId);
        mensagem.setRemetenteId(remetenteId);
        mensagem.setAssunto(assunto.trim());
        mensagem.setCorpo(corpo.trim());

        List<MensagemInternaDestinatario> destinatarios = destinatarioIds.stream()
            .distinct()
            .map(destinatarioId -> {
                MensagemInternaDestinatario destinatario = new MensagemInternaDestinatario();
                destinatario.setId(UUID.randomUUID());
                destinatario.setTenantId(tenantId);
                destinatario.setMensagemId(mensagem.getId());
                destinatario.setDestinatarioId(destinatarioId);
                return destinatario;
            })
            .toList();
        mensagem.setDestinatarios(destinatarios);

        return mensagemInternaRepository.salvar(mensagem);
    }

    private void validarCampos(String assunto, String corpo, List<UUID> destinatarioIds) {
        if (assunto == null || assunto.isBlank()) {
            throw new ValidationException("Assunto é obrigatório.");
        }
        if (assunto.length() > 200) {
            throw new ValidationException("Assunto deve ter no máximo 200 caracteres.");
        }
        if (corpo == null || corpo.isBlank()) {
            throw new ValidationException("Corpo da mensagem é obrigatório.");
        }
        if (destinatarioIds == null || destinatarioIds.isEmpty()) {
            throw new ValidationException("Informe ao menos um destinatário.");
        }
    }

    private void validarDestinatarios(UUID tenantId, List<UUID> destinatarioIds) {
        for (UUID destinatarioId : destinatarioIds) {
            Usuario usuario = usuarioRepository.buscarPorId(destinatarioId)
                .orElseThrow(() -> new ValidationException("Destinatário não encontrado: " + destinatarioId));
            if (!usuario.getTenantId().equals(tenantId)) {
                throw new ValidationException("Destinatário não pertence ao tenant atual.");
            }
            if (!usuario.isAtivo()) {
                throw new ValidationException("Destinatário inativo: " + usuario.getLogin());
            }
        }
    }

    private UUID usuarioAtualObrigatorio() {
        return usuarioAutenticadoResolver.usuarioIdAtual()
            .orElseThrow(() -> new AutenticacaoException("Usuário não autenticado."));
    }
}
