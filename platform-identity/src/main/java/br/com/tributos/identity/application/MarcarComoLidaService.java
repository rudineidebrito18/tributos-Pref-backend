package br.com.tributos.identity.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.adapters.out.security.UsuarioAutenticadoResolver;
import br.com.tributos.identity.domain.MensagemInternaDestinatario;
import br.com.tributos.identity.domain.MensagemInternaRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class MarcarComoLidaService {

    private final MensagemInternaRepository mensagemInternaRepository;
    private final UsuarioAutenticadoResolver usuarioAutenticadoResolver;

    public MarcarComoLidaService(
        MensagemInternaRepository mensagemInternaRepository,
        UsuarioAutenticadoResolver usuarioAutenticadoResolver
    ) {
        this.mensagemInternaRepository = mensagemInternaRepository;
        this.usuarioAutenticadoResolver = usuarioAutenticadoResolver;
    }

    public void executar(UUID mensagemId) {
        UUID usuarioId = usuarioAtualObrigatorio();
        MensagemInternaDestinatario destinatario = mensagemInternaRepository
            .buscarDestinatario(mensagemId, usuarioId)
            .orElseThrow(() -> new NotFoundException("Mensagem não encontrada."));

        if (destinatario.getLidaEm() == null) {
            destinatario.setLidaEm(Instant.now());
            mensagemInternaRepository.salvarDestinatario(destinatario);
        }
    }

    private UUID usuarioAtualObrigatorio() {
        return usuarioAutenticadoResolver.usuarioIdAtual()
            .orElseThrow(() -> new AutenticacaoException("Usuário não autenticado."));
    }
}
