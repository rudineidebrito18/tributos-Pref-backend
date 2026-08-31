package br.com.tributos.identity.adapters.in.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.identity.adapters.in.web.dto.UsuarioResumoResponse;
import br.com.tributos.identity.application.ListarUsuariosAtivosService;

@RestController
@RequestMapping("/api/plataforma/usuarios")
public class UsuarioPlataformaController {

    private final ListarUsuariosAtivosService listarUsuariosAtivosService;

    public UsuarioPlataformaController(ListarUsuariosAtivosService listarUsuariosAtivosService) {
        this.listarUsuariosAtivosService = listarUsuariosAtivosService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    public List<UsuarioResumoResponse> listarAtivos() {
        return listarUsuariosAtivosService.executar().stream()
            .map(UsuarioResumoResponse::de)
            .toList();
    }
}
