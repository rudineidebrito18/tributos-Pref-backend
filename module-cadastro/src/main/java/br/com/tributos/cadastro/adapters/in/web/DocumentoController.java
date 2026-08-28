package br.com.tributos.cadastro.adapters.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.tributos.cadastro.adapters.in.web.dto.DocumentoResponse;
import br.com.tributos.cadastro.application.AnexarDocumentoService;
import br.com.tributos.cadastro.application.BaixarDocumentoService;
import br.com.tributos.cadastro.application.ExcluirDocumentoService;
import br.com.tributos.cadastro.application.ListarDocumentosService;
import br.com.tributos.kernel.exception.ValidationException;

@RestController
@RequestMapping("/api/cadastro/pessoas/{pessoaId}/documentos")
public class DocumentoController {

    private final ListarDocumentosService listarDocumentosService;
    private final AnexarDocumentoService anexarDocumentoService;
    private final BaixarDocumentoService baixarDocumentoService;
    private final ExcluirDocumentoService excluirDocumentoService;

    public DocumentoController(
        ListarDocumentosService listarDocumentosService,
        AnexarDocumentoService anexarDocumentoService,
        BaixarDocumentoService baixarDocumentoService,
        ExcluirDocumentoService excluirDocumentoService
    ) {
        this.listarDocumentosService = listarDocumentosService;
        this.anexarDocumentoService = anexarDocumentoService;
        this.baixarDocumentoService = baixarDocumentoService;
        this.excluirDocumentoService = excluirDocumentoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<DocumentoResponse> listar(@PathVariable UUID pessoaId) {
        return listarDocumentosService.executar(pessoaId).stream().map(DocumentoResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoResponse> anexar(
        @PathVariable UUID pessoaId,
        @RequestParam String tipo,
        @RequestParam("arquivo") MultipartFile arquivo
    ) {
        if (tipo == null || tipo.isBlank()) {
            throw new ValidationException("Informe o tipo do documento.");
        }
        DocumentoResponse resposta = DocumentoResponse.de(anexarDocumentoService.executar(pessoaId, tipo, arquivo));
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{documentoId}/download")
    public ResponseEntity<Resource> baixar(@PathVariable UUID pessoaId, @PathVariable UUID documentoId) {
        BaixarDocumentoService.ArquivoParaDownload arquivo = baixarDocumentoService.executar(pessoaId, documentoId);
        return ResponseEntity.ok()
            .contentType(arquivo.conteudoTipo())
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arquivo.nomeArquivo() + "\"")
            .body(arquivo.recurso());
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @DeleteMapping("/{documentoId}")
    public ResponseEntity<Void> excluir(@PathVariable UUID pessoaId, @PathVariable UUID documentoId) {
        excluirDocumentoService.executar(pessoaId, documentoId);
        return ResponseEntity.noContent().build();
    }
}
