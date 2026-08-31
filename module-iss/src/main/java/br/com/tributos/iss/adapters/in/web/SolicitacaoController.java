package br.com.tributos.iss.adapters.in.web;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iss.adapters.in.web.dto.AbrirSolicitacaoRequest;
import br.com.tributos.iss.adapters.in.web.dto.AlterarStatusSolicitacaoRequest;
import br.com.tributos.iss.adapters.in.web.dto.SolicitacaoResponse;
import br.com.tributos.iss.application.AbrirSolicitacaoService;
import br.com.tributos.iss.application.AlterarStatusSolicitacaoService;
import br.com.tributos.iss.application.ListarSolicitacoesService;
import br.com.tributos.iss.application.ListarSolicitacoesService.SolicitacaoListagemItem;

@RestController
@RequestMapping("/api/iss/solicitacoes")
public class SolicitacaoController {

    private final AbrirSolicitacaoService abrirSolicitacaoService;
    private final ListarSolicitacoesService listarSolicitacoesService;
    private final AlterarStatusSolicitacaoService alterarStatusSolicitacaoService;

    public SolicitacaoController(
        AbrirSolicitacaoService abrirSolicitacaoService,
        ListarSolicitacoesService listarSolicitacoesService,
        AlterarStatusSolicitacaoService alterarStatusSolicitacaoService
    ) {
        this.abrirSolicitacaoService = abrirSolicitacaoService;
        this.listarSolicitacoesService = listarSolicitacoesService;
        this.alterarStatusSolicitacaoService = alterarStatusSolicitacaoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<SolicitacaoResumoResponse> listar(
        @RequestParam(required = false) UUID tipoSolicitacaoId,
        @RequestParam(required = false) UUID statusSolicitacaoId,
        Pageable pageable
    ) {
        return listarSolicitacoesService.executar(tipoSolicitacaoId, statusSolicitacaoId, pageable)
            .map(SolicitacaoResumoResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<SolicitacaoResponse> abrir(@Valid @RequestBody AbrirSolicitacaoRequest request) {
        SolicitacaoResponse resposta = SolicitacaoResponse.de(abrirSolicitacaoService.executar(
            request.tipoSolicitacaoId(),
            request.statusSolicitacaoId(),
            request.descricao(),
            request.dataHora()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PatchMapping("/{id}/status")
    public SolicitacaoResponse alterarStatus(
        @PathVariable UUID id,
        @Valid @RequestBody AlterarStatusSolicitacaoRequest request
    ) {
        return SolicitacaoResponse.de(alterarStatusSolicitacaoService.executar(id, request.statusSolicitacaoId()));
    }

    public record SolicitacaoResumoResponse(
        UUID id,
        String usuario,
        String descricao,
        String tipoSolicitacao,
        String status,
        java.time.Instant data
    ) {
        static SolicitacaoResumoResponse de(SolicitacaoListagemItem item) {
            return new SolicitacaoResumoResponse(
                item.id(),
                item.usuario(),
                item.descricao(),
                item.tipoSolicitacao(),
                item.status(),
                item.data()
            );
        }
    }
}
