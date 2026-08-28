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

import br.com.tributos.iss.adapters.in.web.dto.SalvarServicoRequest;
import br.com.tributos.iss.adapters.in.web.dto.ServicoResponse;
import br.com.tributos.iss.application.GerenciarServicoService;

@RestController
@RequestMapping("/api/iss/servicos")
public class ServicoController {

    private final GerenciarServicoService gerenciarServicoService;

    public ServicoController(GerenciarServicoService gerenciarServicoService) {
        this.gerenciarServicoService = gerenciarServicoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<ServicoResponse> listar() {
        return gerenciarServicoService.listar().stream().map(ServicoResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public ServicoResponse buscar(@PathVariable UUID id) {
        return ServicoResponse.de(gerenciarServicoService.buscar(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping
    public ResponseEntity<ServicoResponse> criar(@Valid @RequestBody SalvarServicoRequest request) {
        ServicoResponse resposta = ServicoResponse.de(
            gerenciarServicoService.criar(
                request.codigoLc116(),
                request.descricao(),
                request.aliquotaMinima(),
                request.aliquotaMaxima(),
                request.ativo()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/{id}")
    public ServicoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody SalvarServicoRequest request) {
        return ServicoResponse.de(
            gerenciarServicoService.atualizar(
                id,
                request.codigoLc116(),
                request.descricao(),
                request.aliquotaMinima(),
                request.aliquotaMaxima(),
                request.ativo()
            )
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        gerenciarServicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
