package br.com.tributos.iptu.adapters.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iptu.adapters.in.web.dto.AtualizarCatalogoIptuRequest;
import br.com.tributos.iptu.adapters.in.web.dto.CatalogoIptuResponse;
import br.com.tributos.iptu.adapters.in.web.dto.SalvarCatalogoIptuRequest;
import br.com.tributos.iptu.application.GerenciarCatalogoIptuService;
import br.com.tributos.iptu.domain.TipoCatalogoIptu;

@RestController
@RequestMapping("/api/iptu/catalogos")
public class CatalogoIptuController {

    private final GerenciarCatalogoIptuService gerenciarCatalogoIptuService;

    public CatalogoIptuController(GerenciarCatalogoIptuService gerenciarCatalogoIptuService) {
        this.gerenciarCatalogoIptuService = gerenciarCatalogoIptuService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{tipo}")
    public List<CatalogoIptuResponse> listar(@PathVariable String tipo) {
        TipoCatalogoIptu tipoCatalogo = TipoCatalogoIptu.fromPath(tipo);
        return gerenciarCatalogoIptuService.listar(tipoCatalogo).stream().map(CatalogoIptuResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{tipo}/{id}")
    public CatalogoIptuResponse buscar(@PathVariable String tipo, @PathVariable UUID id) {
        TipoCatalogoIptu tipoCatalogo = TipoCatalogoIptu.fromPath(tipo);
        return CatalogoIptuResponse.de(gerenciarCatalogoIptuService.buscar(tipoCatalogo, id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/{tipo}")
    public ResponseEntity<CatalogoIptuResponse> criar(
        @PathVariable String tipo,
        @Valid @RequestBody SalvarCatalogoIptuRequest request
    ) {
        TipoCatalogoIptu tipoCatalogo = TipoCatalogoIptu.fromPath(tipo);
        CatalogoIptuResponse resposta = CatalogoIptuResponse.de(
            gerenciarCatalogoIptuService.criar(tipoCatalogo, request.nome(), request.ativo())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/{tipo}/{id}")
    public CatalogoIptuResponse atualizar(
        @PathVariable String tipo,
        @PathVariable UUID id,
        @Valid @RequestBody AtualizarCatalogoIptuRequest request
    ) {
        TipoCatalogoIptu tipoCatalogo = TipoCatalogoIptu.fromPath(tipo);
        return CatalogoIptuResponse.de(
            gerenciarCatalogoIptuService.atualizar(tipoCatalogo, id, request.nome(), request.ativo())
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/{tipo}/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String tipo, @PathVariable UUID id) {
        TipoCatalogoIptu tipoCatalogo = TipoCatalogoIptu.fromPath(tipo);
        gerenciarCatalogoIptuService.excluir(tipoCatalogo, id);
        return ResponseEntity.noContent().build();
    }
}
