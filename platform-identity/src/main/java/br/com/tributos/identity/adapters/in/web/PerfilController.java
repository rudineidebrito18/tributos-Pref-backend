package br.com.tributos.identity.adapters.in.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.tributos.identity.adapters.in.web.dto.AtualizarPerfilRequest;
import br.com.tributos.identity.adapters.in.web.dto.PerfilResponse;
import br.com.tributos.identity.adapters.out.security.UsuarioAutenticadoResolver;
import br.com.tributos.identity.application.AnexarFotoPerfilService;
import br.com.tributos.identity.application.AtualizarPerfilService;
import br.com.tributos.identity.application.BuscarPerfilService;
import br.com.tributos.kernel.exception.AutenticacaoException;

@RestController
@RequestMapping("/api/plataforma/perfil")
public class PerfilController {

    private final UsuarioAutenticadoResolver usuarioAutenticadoResolver;
    private final BuscarPerfilService buscarPerfilService;
    private final AtualizarPerfilService atualizarPerfilService;
    private final AnexarFotoPerfilService anexarFotoPerfilService;

    public PerfilController(
        UsuarioAutenticadoResolver usuarioAutenticadoResolver,
        BuscarPerfilService buscarPerfilService,
        AtualizarPerfilService atualizarPerfilService,
        AnexarFotoPerfilService anexarFotoPerfilService
    ) {
        this.usuarioAutenticadoResolver = usuarioAutenticadoResolver;
        this.buscarPerfilService = buscarPerfilService;
        this.atualizarPerfilService = atualizarPerfilService;
        this.anexarFotoPerfilService = anexarFotoPerfilService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    public PerfilResponse obter() {
        UUID usuarioId = usuarioAutenticadoObrigatorio();
        return PerfilResponse.de(buscarPerfilService.executar(usuarioId));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    public PerfilResponse atualizar(@RequestBody AtualizarPerfilRequest request) {
        UUID usuarioId = usuarioAutenticadoObrigatorio();
        return PerfilResponse.de(atualizarPerfilService.executar(
            usuarioId,
            request.nome(),
            request.login(),
            request.email(),
            request.password1(),
            request.password2()
        ));
    }

    @PostMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    public ResponseEntity<PerfilResponse> anexarFoto(@RequestParam("arquivo") MultipartFile arquivo) {
        UUID usuarioId = usuarioAutenticadoObrigatorio();
        PerfilResponse resposta = PerfilResponse.de(anexarFotoPerfilService.executar(usuarioId, arquivo));
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    private UUID usuarioAutenticadoObrigatorio() {
        return usuarioAutenticadoResolver.usuarioIdAtual()
            .orElseThrow(() -> new AutenticacaoException("Usuário não autenticado."));
    }
}
