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

import br.com.tributos.cadastro.adapters.in.web.dto.BairroResponse;
import br.com.tributos.cadastro.adapters.in.web.dto.SalvarBairroRequest;
import br.com.tributos.cadastro.application.GerenciarBairroService;

@RestController
@RequestMapping("/api/cadastro/bairros")
public class BairroController {

    private final GerenciarBairroService gerenciarBairroService;

    public BairroController(GerenciarBairroService gerenciarBairroService) {
        this.gerenciarBairroService = gerenciarBairroService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<BairroResponse> listar(@RequestParam UUID cidadeId) {
        return gerenciarBairroService.listar(cidadeId).stream().map(BairroResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public BairroResponse buscar(@PathVariable UUID id) {
        return BairroResponse.de(gerenciarBairroService.buscar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PostMapping
    public ResponseEntity<BairroResponse> criar(@Valid @RequestBody SalvarBairroRequest request) {
        BairroResponse resposta = BairroResponse.de(
            gerenciarBairroService.criar(
                request.cidadeId(), request.nome(), request.zonaFiscalId(), request.valorTerreno()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PutMapping("/{id}")
    public BairroResponse atualizar(@PathVariable UUID id, @Valid @RequestBody SalvarBairroRequest request) {
        return BairroResponse.de(
            gerenciarBairroService.atualizar(id, request.nome(), request.zonaFiscalId(), request.valorTerreno())
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        gerenciarBairroService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
