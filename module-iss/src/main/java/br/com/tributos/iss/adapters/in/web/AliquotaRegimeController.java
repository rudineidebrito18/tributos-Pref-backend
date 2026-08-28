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

import br.com.tributos.iss.adapters.in.web.dto.AliquotaRegimeResponse;
import br.com.tributos.iss.adapters.in.web.dto.CalcularAliquotaRequest;
import br.com.tributos.iss.adapters.in.web.dto.CalcularAliquotaResponse;
import br.com.tributos.iss.adapters.in.web.dto.SalvarAliquotaRegimeRequest;
import br.com.tributos.iss.application.CalcularAliquotaEfetivaService;
import br.com.tributos.iss.application.GerenciarAliquotaRegimeService;

@RestController
@RequestMapping("/api/iss/regimes/{regimeId}/aliquotas")
public class AliquotaRegimeController {

    private final GerenciarAliquotaRegimeService gerenciarAliquotaRegimeService;
    private final CalcularAliquotaEfetivaService calcularAliquotaEfetivaService;

    public AliquotaRegimeController(
        GerenciarAliquotaRegimeService gerenciarAliquotaRegimeService,
        CalcularAliquotaEfetivaService calcularAliquotaEfetivaService
    ) {
        this.gerenciarAliquotaRegimeService = gerenciarAliquotaRegimeService;
        this.calcularAliquotaEfetivaService = calcularAliquotaEfetivaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<AliquotaRegimeResponse> listar(@PathVariable UUID regimeId) {
        return gerenciarAliquotaRegimeService.listarPorRegime(regimeId).stream()
            .map(AliquotaRegimeResponse::de)
            .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public AliquotaRegimeResponse buscar(@PathVariable UUID regimeId, @PathVariable UUID id) {
        return AliquotaRegimeResponse.de(gerenciarAliquotaRegimeService.buscar(regimeId, id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping
    public ResponseEntity<AliquotaRegimeResponse> criar(
        @PathVariable UUID regimeId,
        @Valid @RequestBody SalvarAliquotaRegimeRequest request
    ) {
        AliquotaRegimeResponse resposta = AliquotaRegimeResponse.de(
            gerenciarAliquotaRegimeService.criar(
                regimeId,
                request.faixaReceitaMin(),
                request.faixaReceitaMax(),
                request.aliquotaNominal(),
                request.parcelaDeduzir(),
                request.percentualIss(),
                request.competenciaVigencia(),
                request.anexoSimples()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/{id}")
    public AliquotaRegimeResponse atualizar(
        @PathVariable UUID regimeId,
        @PathVariable UUID id,
        @Valid @RequestBody SalvarAliquotaRegimeRequest request
    ) {
        return AliquotaRegimeResponse.de(
            gerenciarAliquotaRegimeService.atualizar(
                regimeId,
                id,
                request.faixaReceitaMin(),
                request.faixaReceitaMax(),
                request.aliquotaNominal(),
                request.parcelaDeduzir(),
                request.percentualIss(),
                request.competenciaVigencia(),
                request.anexoSimples()
            )
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID regimeId, @PathVariable UUID id) {
        gerenciarAliquotaRegimeService.excluir(regimeId, id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @PostMapping("/calcular")
    public CalcularAliquotaResponse calcular(
        @PathVariable UUID regimeId,
        @Valid @RequestBody CalcularAliquotaRequest request
    ) {
        return CalcularAliquotaResponse.de(
            calcularAliquotaEfetivaService.calcular(
                regimeId,
                request.receitaBrutaAcumulada12Meses(),
                request.competencia()
            )
        );
    }
}
