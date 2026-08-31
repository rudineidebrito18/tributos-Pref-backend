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

import br.com.tributos.iss.adapters.in.web.dto.CertidaoIssResponse;
import br.com.tributos.iss.adapters.in.web.dto.EmitirCertidaoAvulsaRequest;
import br.com.tributos.iss.adapters.in.web.dto.EmitirCertidaoRequest;
import br.com.tributos.iss.application.EmitirCertidaoComando;
import br.com.tributos.iss.application.EmitirCertidaoService;
import br.com.tributos.iss.application.GeradorPdfIssService;
import br.com.tributos.iss.application.ListarCertidoesService;

@RestController
@RequestMapping("/api/iss/certidoes")
public class CertidaoController {

    private final ListarCertidoesService listarCertidoesService;
    private final EmitirCertidaoService emitirCertidaoService;
    private final GeradorPdfIssService geradorPdfIssService;

    public CertidaoController(
        ListarCertidoesService listarCertidoesService,
        EmitirCertidaoService emitirCertidaoService,
        GeradorPdfIssService geradorPdfIssService
    ) {
        this.listarCertidoesService = listarCertidoesService;
        this.emitirCertidaoService = emitirCertidaoService;
        this.geradorPdfIssService = geradorPdfIssService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<CertidaoIssResponse> listar(
        @RequestParam(required = false) UUID contribuinteId,
        Pageable pageable
    ) {
        return listarCertidoesService.executar(contribuinteId, pageable).map(CertidaoIssResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<CertidaoIssResponse> emitir(@Valid @RequestBody EmitirCertidaoRequest request) {
        CertidaoIssResponse resposta = CertidaoIssResponse.de(
            emitirCertidaoService.executar(paraComando(request, false))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping("/avulsa")
    public ResponseEntity<CertidaoIssResponse> emitirAvulsa(@Valid @RequestBody EmitirCertidaoAvulsaRequest request) {
        CertidaoIssResponse resposta = CertidaoIssResponse.de(
            emitirCertidaoService.executar(new EmitirCertidaoComando(
                request.contribuinteId(),
                request.tipo(),
                request.validade(),
                request.situacaoCndId(),
                request.observacao(),
                true,
                request.tributos()
            ))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping("/emitir")
    public ResponseEntity<CertidaoIssResponse> emitirLegado(@Valid @RequestBody EmitirCertidaoRequest request) {
        return emitir(paraComandoLegado(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        byte[] pdf = geradorPdfIssService.gerarPdfCertidao(id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    private ResponseEntity<CertidaoIssResponse> emitir(EmitirCertidaoComando comando) {
        CertidaoIssResponse resposta = CertidaoIssResponse.de(emitirCertidaoService.executar(comando));
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    private static EmitirCertidaoComando paraComando(EmitirCertidaoRequest request, boolean avulsa) {
        return new EmitirCertidaoComando(
            request.contribuinteId(),
            request.tipo(),
            request.validade(),
            request.situacaoCndId(),
            request.observacao(),
            avulsa,
            request.tributos()
        );
    }

    private static EmitirCertidaoComando paraComandoLegado(EmitirCertidaoRequest request) {
        return paraComando(request, false);
    }
}
