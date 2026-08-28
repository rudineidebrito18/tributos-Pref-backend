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

import br.com.tributos.iss.adapters.in.web.dto.AnalisarCredenciamentoRequest;
import br.com.tributos.iss.adapters.in.web.dto.SolicitacaoCredenciamentoResponse;
import br.com.tributos.iss.adapters.in.web.dto.SolicitarCredenciamentoRequest;
import br.com.tributos.iss.application.AnalisarCredenciamentoService;
import br.com.tributos.iss.application.ListarSolicitacoesCredenciamentoService;
import br.com.tributos.iss.application.SolicitarCredenciamentoService;

@RestController
@RequestMapping("/api/iss/credenciamento")
public class CredenciamentoController {

    private final SolicitarCredenciamentoService solicitarCredenciamentoService;
    private final ListarSolicitacoesCredenciamentoService listarSolicitacoesCredenciamentoService;
    private final AnalisarCredenciamentoService analisarCredenciamentoService;

    public CredenciamentoController(
        SolicitarCredenciamentoService solicitarCredenciamentoService,
        ListarSolicitacoesCredenciamentoService listarSolicitacoesCredenciamentoService,
        AnalisarCredenciamentoService analisarCredenciamentoService
    ) {
        this.solicitarCredenciamentoService = solicitarCredenciamentoService;
        this.listarSolicitacoesCredenciamentoService = listarSolicitacoesCredenciamentoService;
        this.analisarCredenciamentoService = analisarCredenciamentoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping("/solicitar")
    public ResponseEntity<SolicitacaoCredenciamentoResponse> solicitar(
        @Valid @RequestBody SolicitarCredenciamentoRequest request
    ) {
        SolicitacaoCredenciamentoResponse resposta = SolicitacaoCredenciamentoResponse.de(
            solicitarCredenciamentoService.executar(request.contribuinteId())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/solicitacoes")
    public Page<SolicitacaoCredenciamentoResponse> listar(Pageable pageable) {
        return listarSolicitacoesCredenciamentoService.executar(pageable)
            .map(SolicitacaoCredenciamentoResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PostMapping("/solicitacoes/{id}/aprovar")
    public SolicitacaoCredenciamentoResponse aprovar(
        @PathVariable UUID id,
        @Valid @RequestBody AnalisarCredenciamentoRequest request
    ) {
        return SolicitacaoCredenciamentoResponse.de(
            analisarCredenciamentoService.aprovar(id, request.observacao())
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PostMapping("/solicitacoes/{id}/negar")
    public SolicitacaoCredenciamentoResponse negar(
        @PathVariable UUID id,
        @Valid @RequestBody AnalisarCredenciamentoRequest request
    ) {
        return SolicitacaoCredenciamentoResponse.de(
            analisarCredenciamentoService.negar(id, request.observacao())
        );
    }
}
