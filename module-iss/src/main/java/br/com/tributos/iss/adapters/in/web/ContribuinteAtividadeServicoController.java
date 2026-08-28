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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iss.adapters.in.web.dto.ContribuinteAtividadeServicoResponse;
import br.com.tributos.iss.adapters.in.web.dto.VincularAtividadeServicoRequest;
import br.com.tributos.iss.application.VincularAtividadeServicoService;

@RestController
@RequestMapping("/api/iss/contribuintes/{contribuinteId}/atividades-servicos")
public class ContribuinteAtividadeServicoController {

    private final VincularAtividadeServicoService vincularAtividadeServicoService;

    public ContribuinteAtividadeServicoController(VincularAtividadeServicoService vincularAtividadeServicoService) {
        this.vincularAtividadeServicoService = vincularAtividadeServicoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<ContribuinteAtividadeServicoResponse> listar(@PathVariable UUID contribuinteId) {
        return vincularAtividadeServicoService.listar(contribuinteId).stream()
            .map(ContribuinteAtividadeServicoResponse::de)
            .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<ContribuinteAtividadeServicoResponse> vincular(
        @PathVariable UUID contribuinteId,
        @Valid @RequestBody VincularAtividadeServicoRequest request
    ) {
        ContribuinteAtividadeServicoResponse resposta = ContribuinteAtividadeServicoResponse.de(
            vincularAtividadeServicoService.vincular(
                contribuinteId,
                request.atividadeId(),
                request.servicoId(),
                request.tributavel()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> desvincular(
        @PathVariable UUID contribuinteId,
        @PathVariable UUID vinculoId
    ) {
        vincularAtividadeServicoService.desvincular(contribuinteId, vinculoId);
        return ResponseEntity.noContent().build();
    }
}
