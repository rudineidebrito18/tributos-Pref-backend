package br.com.tributos.iss.adapters.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iss.adapters.in.web.dto.HistoricoValorAlvaraResponse;
import br.com.tributos.iss.adapters.in.web.dto.SalvarTipoAlvaraRequest;
import br.com.tributos.iss.adapters.in.web.dto.TipoAlvaraResponse;
import br.com.tributos.iss.application.GerenciarTipoAlvaraService;
import br.com.tributos.iss.application.SalvarTipoAlvaraComando;

@RestController
@RequestMapping("/api/iss/tipos-alvara")
public class TipoAlvaraController {

    private final GerenciarTipoAlvaraService gerenciarTipoAlvaraService;

    public TipoAlvaraController(GerenciarTipoAlvaraService gerenciarTipoAlvaraService) {
        this.gerenciarTipoAlvaraService = gerenciarTipoAlvaraService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<TipoAlvaraResponse> listar() {
        return gerenciarTipoAlvaraService.listar().stream().map(TipoAlvaraResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}/historico-valores")
    public List<HistoricoValorAlvaraResponse> historicoValores(@PathVariable UUID id) {
        return gerenciarTipoAlvaraService.listarHistoricoValores(id).stream()
            .map(HistoricoValorAlvaraResponse::de)
            .toList();
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping
    public ResponseEntity<TipoAlvaraResponse> criar(@Valid @RequestBody SalvarTipoAlvaraRequest request) {
        TipoAlvaraResponse resposta = TipoAlvaraResponse.de(
            gerenciarTipoAlvaraService.criar(paraComando(request))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/{id}")
    public TipoAlvaraResponse atualizar(@PathVariable UUID id, @Valid @RequestBody SalvarTipoAlvaraRequest request) {
        return TipoAlvaraResponse.de(gerenciarTipoAlvaraService.atualizar(id, paraComando(request)));
    }

    private static SalvarTipoAlvaraComando paraComando(SalvarTipoAlvaraRequest request) {
        return new SalvarTipoAlvaraComando(
            request.nome(),
            request.valorBase(),
            request.diasValidade(),
            request.ativo() != null ? request.ativo() : true,
            request.anoVigencia(),
            request.identificacaoModeloDocumento(),
            request.permiteValorDinamico() != null ? request.permiteValorDinamico() : false,
            request.permiteCalculoValor() != null ? request.permiteCalculoValor() : false,
            request.unidadeMedidaDescritivo(),
            request.habilitarValidade() != null ? request.habilitarValidade() : true,
            request.habilitarCalculoVencimento() != null ? request.habilitarCalculoVencimento() : false,
            request.baseVencimento(),
            request.diasMesesVencimento(),
            request.titulo(),
            request.secretaria(),
            request.cargo(),
            request.assinaturaDocumentoId()
        );
    }
}
