package br.com.tributos.iss.adapters.in.web;

import java.time.YearMonth;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iss.adapters.in.web.dto.CancelarNotaFiscalRequest;
import br.com.tributos.iss.adapters.in.web.dto.EmitirNotaFiscalRequest;
import br.com.tributos.iss.adapters.in.web.dto.NotaFiscalListagemResponse;
import br.com.tributos.iss.adapters.in.web.dto.NotaFiscalResponse;
import br.com.tributos.iss.adapters.in.web.dto.SubstituirNotaFiscalRequest;
import br.com.tributos.iss.application.CancelarNotaFiscalService;
import br.com.tributos.iss.application.EmitirNotaFiscalComando;
import br.com.tributos.iss.application.EmitirNotaFiscalService;
import br.com.tributos.iss.application.ListarNotasFiscaisService;
import br.com.tributos.iss.application.SubstituirNotaFiscalService;

@RestController
@RequestMapping("/api/iss/notas-fiscais")
public class NotaFiscalController {

    private final ListarNotasFiscaisService listarNotasFiscaisService;
    private final EmitirNotaFiscalService emitirNotaFiscalService;
    private final CancelarNotaFiscalService cancelarNotaFiscalService;
    private final SubstituirNotaFiscalService substituirNotaFiscalService;

    public NotaFiscalController(
        ListarNotasFiscaisService listarNotasFiscaisService,
        EmitirNotaFiscalService emitirNotaFiscalService,
        CancelarNotaFiscalService cancelarNotaFiscalService,
        SubstituirNotaFiscalService substituirNotaFiscalService
    ) {
        this.listarNotasFiscaisService = listarNotasFiscaisService;
        this.emitirNotaFiscalService = emitirNotaFiscalService;
        this.cancelarNotaFiscalService = cancelarNotaFiscalService;
        this.substituirNotaFiscalService = substituirNotaFiscalService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<NotaFiscalListagemResponse> listar(
        @RequestParam(required = false) UUID contribuinteId,
        @RequestParam(required = false) UUID tomadorId,
        @RequestParam(required = false) YearMonth competencia,
        Pageable pageable
    ) {
        return listarNotasFiscaisService.executar(contribuinteId, tomadorId, competencia, pageable)
            .map(NotaFiscalListagemResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping("/emitir")
    public ResponseEntity<NotaFiscalResponse> emitir(@Valid @RequestBody EmitirNotaFiscalRequest request) {
        NotaFiscalResponse resposta = NotaFiscalResponse.de(
            emitirNotaFiscalService.executar(paraComando(request))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping("/{id}/cancelar")
    public NotaFiscalResponse cancelar(
        @PathVariable UUID id,
        @Valid @RequestBody CancelarNotaFiscalRequest request
    ) {
        return NotaFiscalResponse.de(cancelarNotaFiscalService.executar(id, request.motivo()));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping("/{id}/substituir")
    public ResponseEntity<NotaFiscalResponse> substituir(
        @PathVariable UUID id,
        @Valid @RequestBody SubstituirNotaFiscalRequest request
    ) {
        NotaFiscalResponse resposta = NotaFiscalResponse.de(
            substituirNotaFiscalService.executar(id, paraComando(request))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    private static EmitirNotaFiscalComando paraComando(EmitirNotaFiscalRequest request) {
        return new EmitirNotaFiscalComando(
            request.contribuinteId(),
            request.tomadorId(),
            request.servicoId(),
            request.competencia(),
            request.valorServico(),
            request.valorDeducoes(),
            request.receitaBrutaAcumulada12Meses(),
            request.serie(),
            request.atividadeId(),
            request.valorIr(),
            request.valorPis(),
            request.valorCofins(),
            request.valorCsll(),
            request.valorInss(),
            request.issRetidoFonte()
        );
    }

    private static EmitirNotaFiscalComando paraComando(SubstituirNotaFiscalRequest request) {
        return new EmitirNotaFiscalComando(
            null,
            request.tomadorId(),
            request.servicoId(),
            request.competencia(),
            request.valorServico(),
            request.valorDeducoes(),
            request.receitaBrutaAcumulada12Meses(),
            request.serie(),
            request.atividadeId(),
            request.valorIr(),
            request.valorPis(),
            request.valorCofins(),
            request.valorCsll(),
            request.valorInss(),
            request.issRetidoFonte()
        );
    }
}
