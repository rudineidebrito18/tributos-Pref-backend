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

import br.com.tributos.iss.adapters.in.web.dto.AtividadeResponse;
import br.com.tributos.iss.adapters.in.web.dto.SalvarAtividadeRequest;
import br.com.tributos.iss.application.GerenciarAtividadeService;

@RestController
@RequestMapping("/api/iss/atividades")
public class AtividadeController {

    private final GerenciarAtividadeService gerenciarAtividadeService;

    public AtividadeController(GerenciarAtividadeService gerenciarAtividadeService) {
        this.gerenciarAtividadeService = gerenciarAtividadeService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<AtividadeResponse> listar() {
        return gerenciarAtividadeService.listar().stream().map(AtividadeResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public AtividadeResponse buscar(@PathVariable UUID id) {
        return AtividadeResponse.de(gerenciarAtividadeService.buscar(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping
    public ResponseEntity<AtividadeResponse> criar(@Valid @RequestBody SalvarAtividadeRequest request) {
        AtividadeResponse resposta = AtividadeResponse.de(
            gerenciarAtividadeService.criar(request.codigo(), request.descricao(), request.ativo())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/{id}")
    public AtividadeResponse atualizar(@PathVariable UUID id, @Valid @RequestBody SalvarAtividadeRequest request) {
        return AtividadeResponse.de(
            gerenciarAtividadeService.atualizar(id, request.codigo(), request.descricao(), request.ativo())
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        gerenciarAtividadeService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
