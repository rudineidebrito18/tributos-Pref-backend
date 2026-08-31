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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.cadastro.adapters.in.web.dto.LogradouroResponse;
import br.com.tributos.cadastro.adapters.in.web.dto.SalvarLogradouroRequest;
import br.com.tributos.cadastro.application.GerenciarLogradouroService;

@RestController
@RequestMapping("/api/cadastro/logradouros")
public class LogradouroController {

    private final GerenciarLogradouroService gerenciarLogradouroService;

    public LogradouroController(GerenciarLogradouroService gerenciarLogradouroService) {
        this.gerenciarLogradouroService = gerenciarLogradouroService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<LogradouroResponse> listar(
        @RequestParam UUID cidadeId,
        @RequestParam(required = false) UUID bairroId
    ) {
        return gerenciarLogradouroService.listar(cidadeId, bairroId).stream().map(LogradouroResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public LogradouroResponse buscar(@PathVariable UUID id) {
        return LogradouroResponse.de(gerenciarLogradouroService.buscar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PostMapping
    public ResponseEntity<LogradouroResponse> criar(@Valid @RequestBody SalvarLogradouroRequest request) {
        LogradouroResponse resposta = LogradouroResponse.de(
            gerenciarLogradouroService.criar(
                request.cidadeId(), request.bairroId(), request.tipo(), request.nome(), request.cep()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PutMapping("/{id}")
    public LogradouroResponse atualizar(@PathVariable UUID id, @Valid @RequestBody SalvarLogradouroRequest request) {
        return LogradouroResponse.de(
            gerenciarLogradouroService.atualizar(id, request.bairroId(), request.tipo(), request.nome(), request.cep())
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        gerenciarLogradouroService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
