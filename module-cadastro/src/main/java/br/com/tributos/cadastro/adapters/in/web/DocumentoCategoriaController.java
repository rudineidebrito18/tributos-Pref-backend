package br.com.tributos.cadastro.adapters.in.web;

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

import br.com.tributos.cadastro.adapters.in.web.dto.DocumentoCategoriaResponse;
import br.com.tributos.cadastro.adapters.in.web.dto.SalvarDocumentoCategoriaRequest;
import br.com.tributos.cadastro.application.GerenciarDocumentoCategoriaService;
import br.com.tributos.cadastro.domain.DocumentoCategoria;

@RestController
@RequestMapping("/api/cadastro/documento-categorias")
public class DocumentoCategoriaController {

    private final GerenciarDocumentoCategoriaService gerenciarDocumentoCategoriaService;

    public DocumentoCategoriaController(GerenciarDocumentoCategoriaService gerenciarDocumentoCategoriaService) {
        this.gerenciarDocumentoCategoriaService = gerenciarDocumentoCategoriaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<DocumentoCategoriaResponse> listar() {
        return gerenciarDocumentoCategoriaService.listar().stream()
            .map(DocumentoCategoriaResponse::de)
            .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public DocumentoCategoriaResponse buscar(@PathVariable UUID id) {
        return DocumentoCategoriaResponse.de(gerenciarDocumentoCategoriaService.buscar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PostMapping
    public ResponseEntity<DocumentoCategoriaResponse> criar(@Valid @RequestBody SalvarDocumentoCategoriaRequest request) {
        DocumentoCategoria categoria = gerenciarDocumentoCategoriaService.criar(request.nome());
        return ResponseEntity.status(HttpStatus.CREATED).body(DocumentoCategoriaResponse.de(categoria));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PutMapping("/{id}")
    public DocumentoCategoriaResponse atualizar(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarDocumentoCategoriaRequest request
    ) {
        return DocumentoCategoriaResponse.de(gerenciarDocumentoCategoriaService.atualizar(id, request.nome()));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        gerenciarDocumentoCategoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
