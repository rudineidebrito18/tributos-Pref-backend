package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.adapters.out.security.UsuarioAutenticadoResolver;
import br.com.tributos.identity.domain.MensagemInterna;
import br.com.tributos.identity.domain.MensagemInternaRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BuscarMensagemService {

    private final MensagemInternaRepository mensagemInternaRepository;
    private final UsuarioAutenticadoResolver usuarioAutenticadoResolver;

    public BuscarMensagemService(
        MensagemInternaRepository mensagemInternaRepository,
        UsuarioAutenticadoResolver usuarioAutenticadoResolver
    ) {
        this.mensagemInternaRepository = mensagemInternaRepository;
        this.usuarioAutenticadoResolver = usuarioAutenticadoResolver;
    }

    public MensagemInterna executar(UUID mensagemId) {
        UUID usuarioId = usuarioAtualObrigatorio();
        MensagemInterna mensagem = mensagemInternaRepository.buscarPorId(mensagemId)
            .orElseThrow(() -> new NotFoundException("Mensagem não encontrada."));

        if (!podeAcessar(mensagem, usuarioId)) {
            throw new NotFoundException("Mensagem não encontrada.");
        }
        return mensagem;
    }

    private boolean podeAcessar(MensagemInterna mensagem, UUID usuarioId) {
        if (mensagem.getRemetenteId().equals(usuarioId)) {
            return true;
        }
        return mensagem.getDestinatarios().stream()
            .anyMatch(destinatario -> destinatario.getDestinatarioId().equals(usuarioId));
    }

    private UUID usuarioAtualObrigatorio() {
        return usuarioAutenticadoResolver.usuarioIdAtual()
            .orElseThrow(() -> new AutenticacaoException("Usuário não autenticado."));
    }
}
