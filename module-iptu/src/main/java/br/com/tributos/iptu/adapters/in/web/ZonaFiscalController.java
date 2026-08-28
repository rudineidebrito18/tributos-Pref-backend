package br.com.tributos.iptu.adapters.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iptu.adapters.in.web.dto.SalvarZonaFiscalRequest;
import br.com.tributos.iptu.adapters.in.web.dto.ZonaFiscalResponse;
import br.com.tributos.iptu.application.GerenciarZonaFiscalService;

@RestController
@RequestMapping("/api/iptu/zonas-fiscais")
public class ZonaFiscalController {

    private final GerenciarZonaFiscalService gerenciarZonaFiscalService;

    public ZonaFiscalController(GerenciarZonaFiscalService gerenciarZonaFiscalService) {
        this.gerenciarZonaFiscalService = gerenciarZonaFiscalService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<ZonaFiscalResponse> listar() {
        return gerenciarZonaFiscalService.listar().stream().map(ZonaFiscalResponse::de).toList();
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping
    public ResponseEntity<ZonaFiscalResponse> criar(@Valid @RequestBody SalvarZonaFiscalRequest request) {
        ZonaFiscalResponse resposta = ZonaFiscalResponse.de(
            gerenciarZonaFiscalService.criar(request.nome(), request.fatorValorizacao(), request.ativo())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/{id}")
    public ZonaFiscalResponse atualizar(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarZonaFiscalRequest request
    ) {
        return ZonaFiscalResponse.de(
            gerenciarZonaFiscalService.atualizar(id, request.nome(), request.fatorValorizacao(), request.ativo())
        );
    }
}
