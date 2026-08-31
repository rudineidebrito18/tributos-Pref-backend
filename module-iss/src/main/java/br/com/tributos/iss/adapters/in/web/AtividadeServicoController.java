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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iss.adapters.in.web.dto.AtividadeServicoResponse;
import br.com.tributos.iss.adapters.in.web.dto.AtividadeServicoViewResponse;
import br.com.tributos.iss.adapters.in.web.dto.SalvarAtividadeServicoRequest;
import br.com.tributos.iss.application.ListarAtividadeServicoService;
import br.com.tributos.iss.application.SalvarAtividadeServicoComando;
import br.com.tributos.iss.application.SalvarAtividadeServicoService;

@RestController
@RequestMapping("/api/iss/atividades-servicos")
public class AtividadeServicoController {

    private final SalvarAtividadeServicoService salvarAtividadeServicoService;
    private final ListarAtividadeServicoService listarAtividadeServicoService;

    public AtividadeServicoController(
        SalvarAtividadeServicoService salvarAtividadeServicoService,
        ListarAtividadeServicoService listarAtividadeServicoService
    ) {
        this.salvarAtividadeServicoService = salvarAtividadeServicoService;
        this.listarAtividadeServicoService = listarAtividadeServicoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<AtividadeServicoResponse> listar(
        @RequestParam(required = false) String codigoCnae,
        @RequestParam(required = false) String codigoServico,
        Pageable pageable
    ) {
        return listarAtividadeServicoService.executar(codigoCnae, codigoServico, pageable)
            .map(AtividadeServicoResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/view")
    public Page<AtividadeServicoViewResponse> listarView(
        @RequestParam(required = false) String codigoCnae,
        @RequestParam(required = false) String codigoServico,
        Pageable pageable
    ) {
        return listarAtividadeServicoService.executarView(codigoCnae, codigoServico, pageable)
            .map(AtividadeServicoViewResponse::de);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping
    public ResponseEntity<AtividadeServicoResponse> criar(@Valid @RequestBody SalvarAtividadeServicoRequest request) {
        AtividadeServicoResponse resposta = AtividadeServicoResponse.de(
            salvarAtividadeServicoService.criar(paraComando(request))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/{id}")
    public AtividadeServicoResponse atualizar(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarAtividadeServicoRequest request
    ) {
        return AtividadeServicoResponse.de(salvarAtividadeServicoService.atualizar(id, paraComando(request)));
    }

    private static SalvarAtividadeServicoComando paraComando(SalvarAtividadeServicoRequest request) {
        return new SalvarAtividadeServicoComando(
            request.atividadeId(),
            request.servicoId(),
            request.localIncidenciaId(),
            request.aliquota(),
            request.tributavel(),
            request.imune(),
            request.deducao(),
            request.substitutoTributario(),
            request.retencaoFonte(),
            request.regimeEspecial(),
            request.observacao()
        );
    }
}
