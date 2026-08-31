package br.com.tributos.identity.adapters.in.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.tributos.identity.domain.MensagemInterna;
import br.com.tributos.identity.domain.MensagemInternaDestinatario;
import br.com.tributos.identity.domain.Usuario;

public record MensagemInternaResponse(
    UUID id,
    String assunto,
    String corpo,
    UUID remetenteId,
    String remetenteLogin,
    Instant criadoEm,
    List<DestinatarioResponse> destinatarios
) {

    public record DestinatarioResponse(
        UUID id,
        UUID usuarioId,
        String login,
        Instant lidaEm,
        Instant arquivadaEm
    ) {
    }

    public static MensagemInternaResponse de(MensagemInterna mensagem, List<Usuario> usuarios) {
        Map<UUID, Usuario> usuariosPorId = usuarios.stream()
            .collect(Collectors.toMap(Usuario::getId, Function.identity()));

        String remetenteLogin = usuariosPorId.containsKey(mensagem.getRemetenteId())
            ? usuariosPorId.get(mensagem.getRemetenteId()).getLogin()
            : "?";

        List<DestinatarioResponse> destinatarios = mensagem.getDestinatarios().stream()
            .map(dest -> paraDestinatario(dest, usuariosPorId))
            .toList();

        return new MensagemInternaResponse(
            mensagem.getId(),
            mensagem.getAssunto(),
            mensagem.getCorpo(),
            mensagem.getRemetenteId(),
            remetenteLogin,
            mensagem.getCriadoEm(),
            destinatarios
        );
    }

    private static DestinatarioResponse paraDestinatario(
        MensagemInternaDestinatario destinatario,
        Map<UUID, Usuario> usuariosPorId
    ) {
        Usuario usuario = usuariosPorId.get(destinatario.getDestinatarioId());
        return new DestinatarioResponse(
            destinatario.getId(),
            destinatario.getDestinatarioId(),
            usuario == null ? "?" : usuario.getLogin(),
            destinatario.getLidaEm(),
            destinatario.getArquivadaEm()
        );
    }
}
