package br.com.tributos.iptu.adapters.in.web;

import java.util.List;

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

import br.com.tributos.iptu.adapters.in.web.dto.AliquotaIptuResponse;
import br.com.tributos.iptu.adapters.in.web.dto.UpsertAliquotaIptuRequest;
import br.com.tributos.iptu.application.GerenciarAliquotaIptuService;

@RestController
@RequestMapping("/api/iptu/exercicios/{exercicio}/aliquotas")
public class AliquotaIptuController {

    private final GerenciarAliquotaIptuService gerenciarAliquotaIptuService;

    public AliquotaIptuController(GerenciarAliquotaIptuService gerenciarAliquotaIptuService) {
        this.gerenciarAliquotaIptuService = gerenciarAliquotaIptuService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<AliquotaIptuResponse> listar(@PathVariable int exercicio) {
        return gerenciarAliquotaIptuService.listarPorExercicio(exercicio).stream()
            .map(AliquotaIptuResponse::de)
            .toList();
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping
    public ResponseEntity<AliquotaIptuResponse> upsert(
        @PathVariable int exercicio,
        @Valid @RequestBody UpsertAliquotaIptuRequest request
    ) {
        AliquotaIptuResponse resposta = AliquotaIptuResponse.de(
            gerenciarAliquotaIptuService.upsert(
                exercicio,
                request.destinacaoId(),
                request.zonaFiscalId(),
                request.aliquota()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
