package br.com.tributos.iss.adapters.in.web;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iss.adapters.in.web.dto.SalvarTomadorRequest;
import br.com.tributos.iss.adapters.in.web.dto.TomadorResponse;
import br.com.tributos.iss.application.BuscarTomadorService;
import br.com.tributos.iss.application.ListarTomadoresService;
import br.com.tributos.iss.application.SalvarTomadorService;

@RestController
@RequestMapping("/api/iss/tomadores")
public class TomadorController {

    private final SalvarTomadorService salvarTomadorService;
    private final ListarTomadoresService listarTomadoresService;
    private final BuscarTomadorService buscarTomadorService;

    public TomadorController(
        SalvarTomadorService salvarTomadorService,
        ListarTomadoresService listarTomadoresService,
        BuscarTomadorService buscarTomadorService
    ) {
        this.salvarTomadorService = salvarTomadorService;
        this.listarTomadoresService = listarTomadoresService;
        this.buscarTomadorService = buscarTomadorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<TomadorResponse> listar(Pageable pageable) {
        return listarTomadoresService.executar(pageable).map(TomadorResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public TomadorResponse buscar(@PathVariable UUID id) {
        return TomadorResponse.de(buscarTomadorService.executar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<TomadorResponse> criar(@Valid @RequestBody SalvarTomadorRequest request) {
        TomadorResponse resposta = TomadorResponse.de(salvarTomadorService.executar(request.pessoaId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
