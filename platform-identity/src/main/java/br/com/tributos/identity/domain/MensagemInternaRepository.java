package br.com.tributos.identity.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MensagemInternaRepository {

    MensagemInterna salvar(MensagemInterna mensagem);

    Optional<MensagemInterna> buscarPorId(UUID id);

    Page<MensagemInterna> listarCaixa(
        UUID usuarioId,
        CaixaMensagem caixa,
        String assuntoPattern,
        String corpoPattern,
        Pageable pageable
    );

    Optional<MensagemInternaDestinatario> buscarDestinatario(UUID mensagemId, UUID destinatarioId);

    void salvarDestinatario(MensagemInternaDestinatario destinatario);
}
