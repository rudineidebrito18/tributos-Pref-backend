package br.com.tributos.iss.adapters.in.web;

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

import br.com.tributos.iss.adapters.in.web.dto.AtualizarCatalogoIssRequest;
import br.com.tributos.iss.adapters.in.web.dto.CatalogoIssResponse;
import br.com.tributos.iss.adapters.in.web.dto.SalvarCatalogoIssRequest;
import br.com.tributos.iss.application.GerenciarCatalogoIssService;
import br.com.tributos.iss.domain.TipoCatalogoIss;

@RestController
@RequestMapping("/api/iss/catalogos")
public class CatalogoIssController {

    private final GerenciarCatalogoIssService gerenciarCatalogoIssService;

    public CatalogoIssController(GerenciarCatalogoIssService gerenciarCatalogoIssService) {
        this.gerenciarCatalogoIssService = gerenciarCatalogoIssService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{tipo}")
    public List<CatalogoIssResponse> listar(@PathVariable String tipo) {
        TipoCatalogoIss tipoCatalogo = TipoCatalogoIss.fromPath(tipo);
        return gerenciarCatalogoIssService.listar(tipoCatalogo).stream().map(CatalogoIssResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{tipo}/{id}")
    public CatalogoIssResponse buscar(@PathVariable String tipo, @PathVariable UUID id) {
        TipoCatalogoIss tipoCatalogo = TipoCatalogoIss.fromPath(tipo);
        return CatalogoIssResponse.de(gerenciarCatalogoIssService.buscar(tipoCatalogo, id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/{tipo}")
    public ResponseEntity<CatalogoIssResponse> criar(
        @PathVariable String tipo,
        @Valid @RequestBody SalvarCatalogoIssRequest request
    ) {
        TipoCatalogoIss tipoCatalogo = TipoCatalogoIss.fromPath(tipo);
        CatalogoIssResponse resposta = CatalogoIssResponse.de(
            gerenciarCatalogoIssService.criar(tipoCatalogo, request.nome(), request.ativo())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/{tipo}/{id}")
    public CatalogoIssResponse atualizar(
        @PathVariable String tipo,
        @PathVariable UUID id,
        @Valid @RequestBody AtualizarCatalogoIssRequest request
    ) {
        TipoCatalogoIss tipoCatalogo = TipoCatalogoIss.fromPath(tipo);
        return CatalogoIssResponse.de(
            gerenciarCatalogoIssService.atualizar(tipoCatalogo, id, request.nome(), request.ativo())
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/{tipo}/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String tipo, @PathVariable UUID id) {
        TipoCatalogoIss tipoCatalogo = TipoCatalogoIss.fromPath(tipo);
        gerenciarCatalogoIssService.excluir(tipoCatalogo, id);
        return ResponseEntity.noContent().build();
    }
}
