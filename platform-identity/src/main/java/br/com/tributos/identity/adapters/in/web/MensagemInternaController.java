package br.com.tributos.identity.adapters.in.web;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.identity.adapters.in.web.dto.EnviarMensagemRequest;
import br.com.tributos.identity.adapters.in.web.dto.MensagemInternaResumoResponse;
import br.com.tributos.identity.adapters.in.web.dto.MensagemInternaResponse;
import br.com.tributos.identity.application.ArquivarMensagemService;
import br.com.tributos.identity.application.BuscarMensagemService;
import br.com.tributos.identity.application.EnviarMensagemService;
import br.com.tributos.identity.application.ListarCaixaService;
import br.com.tributos.identity.application.MarcarComoLidaService;
import br.com.tributos.identity.domain.CaixaMensagem;
import br.com.tributos.identity.domain.MensagemInterna;
import br.com.tributos.identity.domain.MensagemInternaDestinatario;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;

@RestController
@RequestMapping("/api/plataforma/mensagens")
public class MensagemInternaController {

    private final ListarCaixaService listarCaixaService;
    private final EnviarMensagemService enviarMensagemService;
    private final ArquivarMensagemService arquivarMensagemService;
    private final MarcarComoLidaService marcarComoLidaService;
    private final BuscarMensagemService buscarMensagemService;
    private final UsuarioRepository usuarioRepository;

    public MensagemInternaController(
        ListarCaixaService listarCaixaService,
        EnviarMensagemService enviarMensagemService,
        ArquivarMensagemService arquivarMensagemService,
        MarcarComoLidaService marcarComoLidaService,
        BuscarMensagemService buscarMensagemService,
        UsuarioRepository usuarioRepository
    ) {
        this.listarCaixaService = listarCaixaService;
        this.enviarMensagemService = enviarMensagemService;
        this.arquivarMensagemService = arquivarMensagemService;
        this.marcarComoLidaService = marcarComoLidaService;
        this.buscarMensagemService = buscarMensagemService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    public Page<MensagemInternaResumoResponse> listar(
        @RequestParam CaixaMensagem caixa,
        @RequestParam(required = false) String assunto,
        @RequestParam(required = false) String corpo,
        Pageable pageable
    ) {
        return listarCaixaService.executar(caixa, assunto, corpo, pageable)
            .map(MensagemInternaResumoResponse::de);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public MensagemInternaResponse enviar(@Valid @RequestBody EnviarMensagemRequest request) {
        MensagemInterna mensagem = enviarMensagemService.executar(
            request.assunto(),
            request.corpo(),
            request.destinatarioIds()
        );
        return MensagemInternaResponse.de(mensagem, usuariosDaMensagem(mensagem));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    public MensagemInternaResponse buscar(@PathVariable UUID id) {
        MensagemInterna mensagem = buscarMensagemService.executar(id);
        return MensagemInternaResponse.de(mensagem, usuariosDaMensagem(mensagem));
    }

    @PostMapping("/{id}/arquivar")
    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void arquivar(@PathVariable UUID id) {
        arquivarMensagemService.executar(id);
    }

    @PostMapping("/{id}/lida")
    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarComoLida(@PathVariable UUID id) {
        marcarComoLidaService.executar(id);
    }

    private List<Usuario> usuariosDaMensagem(MensagemInterna mensagem) {
        Set<UUID> ids = new LinkedHashSet<>();
        ids.add(mensagem.getRemetenteId());
        for (MensagemInternaDestinatario destinatario : mensagem.getDestinatarios()) {
            ids.add(destinatario.getDestinatarioId());
        }

        List<Usuario> usuarios = new ArrayList<>();
        for (UUID id : ids) {
            usuarioRepository.buscarPorId(id).ifPresent(usuarios::add);
        }
        return usuarios;
    }
}
