package br.com.tributos.iss.adapters.in.web;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import br.com.tributos.iss.adapters.in.web.dto.AlvaraResponse;
import br.com.tributos.iss.adapters.in.web.dto.EmitirAlvaraRequest;
import br.com.tributos.iss.application.EmitirAlvaraService;
import br.com.tributos.iss.application.GeradorPdfIssService;
import br.com.tributos.iss.application.ListarAlvarasService;

@RestController
@RequestMapping("/api/iss/alvaras")
public class AlvaraController {

    private final ListarAlvarasService listarAlvarasService;
    private final EmitirAlvaraService emitirAlvaraService;
    private final GeradorPdfIssService geradorPdfIssService;

    public AlvaraController(
        ListarAlvarasService listarAlvarasService,
        EmitirAlvaraService emitirAlvaraService,
        GeradorPdfIssService geradorPdfIssService
    ) {
        this.listarAlvarasService = listarAlvarasService;
        this.emitirAlvaraService = emitirAlvaraService;
        this.geradorPdfIssService = geradorPdfIssService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<AlvaraResponse> listar(
        @RequestParam(required = false) UUID contribuinteId,
        Pageable pageable
    ) {
        return listarAlvarasService.executar(contribuinteId, pageable).map(AlvaraResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping("/emitir")
    public ResponseEntity<AlvaraResponse> emitir(@Valid @RequestBody EmitirAlvaraRequest request) {
        AlvaraResponse resposta = AlvaraResponse.de(
            emitirAlvaraService.executar(
                request.contribuinteId(),
                request.tipoAlvaraId(),
                request.dataExpedicao(),
                request.situacaoFiscal(),
                request.validade()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        byte[] pdf = geradorPdfIssService.gerarPdfAlvara(id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
