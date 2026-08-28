package br.com.tributos.cadastro.adapters.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;

import br.com.tributos.cadastro.adapters.in.web.dto.CidadeResponse;
import br.com.tributos.cadastro.adapters.in.web.dto.EstadoResponse;
import br.com.tributos.cadastro.application.ListarTerritorioService;

@RestController
@RequestMapping("/api/cadastro/territorio")
public class TerritorioController {

    private final ListarTerritorioService listarTerritorioService;

    public TerritorioController(ListarTerritorioService listarTerritorioService) {
        this.listarTerritorioService = listarTerritorioService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/estados")
    public List<EstadoResponse> listarEstados() {
        return listarTerritorioService.listarEstados().stream().map(EstadoResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/cidades")
    public List<CidadeResponse> listarCidades(@RequestParam String uf) {
        return listarTerritorioService.listarCidades(uf).stream().map(CidadeResponse::de).toList();
    }
}
