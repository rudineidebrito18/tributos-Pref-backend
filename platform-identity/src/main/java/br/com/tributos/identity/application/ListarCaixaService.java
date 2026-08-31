package br.com.tributos.identity.application;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.tributos.identity.adapters.out.security.UsuarioAutenticadoResolver;
import br.com.tributos.identity.domain.CaixaMensagem;
import br.com.tributos.identity.domain.MensagemInterna;
import br.com.tributos.identity.domain.MensagemInternaDestinatario;
import br.com.tributos.identity.domain.MensagemInternaRepository;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;

@Service
public class ListarCaixaService {

    private final MensagemInternaRepository mensagemInternaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAutenticadoResolver usuarioAutenticadoResolver;

    public ListarCaixaService(
        MensagemInternaRepository mensagemInternaRepository,
        UsuarioRepository usuarioRepository,
        UsuarioAutenticadoResolver usuarioAutenticadoResolver
    ) {
        this.mensagemInternaRepository = mensagemInternaRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioAutenticadoResolver = usuarioAutenticadoResolver;
    }

    public Page<MensagemCaixaItem> executar(
        CaixaMensagem caixa,
        String assunto,
        String corpo,
        Pageable pageable
    ) {
        UUID usuarioId = usuarioAtualObrigatorio();
        Page<MensagemInterna> pagina = mensagemInternaRepository.listarCaixa(
            usuarioId,
            caixa,
            padraoLike(assunto),
            padraoLike(corpo),
            pageable
        );

        Map<UUID, Usuario> usuariosPorId = carregarUsuariosRelacionados(pagina, caixa);

        return pagina.map(mensagem -> paraItem(mensagem, caixa, usuarioId, usuariosPorId));
    }

    private Map<UUID, Usuario> carregarUsuariosRelacionados(Page<MensagemInterna> pagina, CaixaMensagem caixa) {
        var ids = pagina.getContent().stream()
            .flatMap(mensagem -> idsRelacionados(mensagem, caixa).stream())
            .collect(Collectors.toSet());

        return ids.stream()
            .map(usuarioRepository::buscarPorId)
            .flatMap(java.util.Optional::stream)
            .collect(Collectors.toMap(Usuario::getId, Function.identity()));
    }

    private java.util.List<UUID> idsRelacionados(MensagemInterna mensagem, CaixaMensagem caixa) {
        if (caixa == CaixaMensagem.ENVIADAS) {
            return mensagem.getDestinatarios().stream()
                .map(MensagemInternaDestinatario::getDestinatarioId)
                .toList();
        }
        return java.util.List.of(mensagem.getRemetenteId());
    }

    private MensagemCaixaItem paraItem(
        MensagemInterna mensagem,
        CaixaMensagem caixa,
        UUID usuarioId,
        Map<UUID, Usuario> usuariosPorId
    ) {
        String usuario = caixa == CaixaMensagem.ENVIADAS
            ? mensagem.getDestinatarios().stream()
                .sorted(Comparator.comparing(MensagemInternaDestinatario::getDestinatarioId))
                .map(dest -> loginDe(usuariosPorId.get(dest.getDestinatarioId())))
                .collect(Collectors.joining(", "))
            : loginDe(usuariosPorId.get(mensagem.getRemetenteId()));

        Instant lidaEm = mensagem.getDestinatarios().stream()
            .filter(dest -> dest.getDestinatarioId().equals(usuarioId))
            .findFirst()
            .map(MensagemInternaDestinatario::getLidaEm)
            .orElse(null);

        Instant arquivadaEm = mensagem.getDestinatarios().stream()
            .filter(dest -> dest.getDestinatarioId().equals(usuarioId))
            .findFirst()
            .map(MensagemInternaDestinatario::getArquivadaEm)
            .orElse(null);

        return new MensagemCaixaItem(
            mensagem.getId(),
            mensagem.getAssunto(),
            usuario,
            mensagem.getCriadoEm(),
            lidaEm,
            arquivadaEm
        );
    }

    private static String loginDe(Usuario usuario) {
        return usuario == null ? "?" : usuario.getLogin();
    }

    private static String padraoLike(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return "%" + valor.trim().toLowerCase() + "%";
    }

    private UUID usuarioAtualObrigatorio() {
        return usuarioAutenticadoResolver.usuarioIdAtual()
            .orElseThrow(() -> new AutenticacaoException("Usuário não autenticado."));
    }

    public record MensagemCaixaItem(
        UUID id,
        String assunto,
        String usuario,
        Instant criadoEm,
        Instant lidaEm,
        Instant arquivadaEm
    ) {
    }
}
