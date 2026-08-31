package br.com.tributos.cadastro.adapters.in.web;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import br.com.tributos.cadastro.adapters.in.web.dto.CompartilharDocumentoRequest;
import br.com.tributos.cadastro.adapters.in.web.dto.DocumentoSistemaResponse;
import br.com.tributos.cadastro.application.AnexarDocumentoSistemaService;
import br.com.tributos.cadastro.application.BaixarDocumentoSistemaService;
import br.com.tributos.cadastro.application.CompartilharDocumentoService;
import br.com.tributos.cadastro.application.ListarDocumentosSistemaService;
import br.com.tributos.cadastro.application.ports.UsuarioAutenticadoPort;
import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoCategoria;
import br.com.tributos.cadastro.domain.DocumentoCategoriaRepository;

@RestController
@RequestMapping("/api/cadastro/documentos-sistema")
public class DocumentoSistemaController {

    private final ListarDocumentosSistemaService listarDocumentosSistemaService;
    private final AnexarDocumentoSistemaService anexarDocumentoSistemaService;
    private final BaixarDocumentoSistemaService baixarDocumentoSistemaService;
    private final CompartilharDocumentoService compartilharDocumentoService;
    private final DocumentoCategoriaRepository documentoCategoriaRepository;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;

    public DocumentoSistemaController(
        ListarDocumentosSistemaService listarDocumentosSistemaService,
        AnexarDocumentoSistemaService anexarDocumentoSistemaService,
        BaixarDocumentoSistemaService baixarDocumentoSistemaService,
        CompartilharDocumentoService compartilharDocumentoService,
        DocumentoCategoriaRepository documentoCategoriaRepository,
        UsuarioAutenticadoPort usuarioAutenticadoPort
    ) {
        this.listarDocumentosSistemaService = listarDocumentosSistemaService;
        this.anexarDocumentoSistemaService = anexarDocumentoSistemaService;
        this.baixarDocumentoSistemaService = baixarDocumentoSistemaService;
        this.compartilharDocumentoService = compartilharDocumentoService;
        this.documentoCategoriaRepository = documentoCategoriaRepository;
        this.usuarioAutenticadoPort = usuarioAutenticadoPort;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<DocumentoSistemaResponse> listar(
        @RequestParam(required = false) String titulo,
        @RequestParam(required = false) UUID categoriaId,
        @RequestParam(required = false) String nomeArquivo,
        Pageable pageable
    ) {
        return listarDocumentosSistemaService.executar(titulo, categoriaId, nomeArquivo, pageable)
            .map(documento -> DocumentoSistemaResponse.de(documento, nomeCategoria(documento.categoriaId())));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoSistemaResponse> anexar(
        @RequestParam String titulo,
        @RequestParam UUID categoriaId,
        @RequestParam("arquivo") MultipartFile arquivo
    ) {
        Documento documento = anexarDocumentoSistemaService.executar(titulo, categoriaId, arquivo);
        DocumentoSistemaResponse resposta = DocumentoSistemaResponse.de(documento, nomeCategoria(documento.categoriaId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> baixar(@PathVariable UUID id) {
        BaixarDocumentoSistemaService.ArquivoParaDownload arquivo = baixarDocumentoSistemaService.executar(id);
        return ResponseEntity.ok()
            .contentType(arquivo.conteudoTipo())
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arquivo.nomeArquivo() + "\"")
            .body(arquivo.recurso());
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @PostMapping("/{id}/compartilhar")
    public ResponseEntity<Void> compartilhar(
        @PathVariable UUID id,
        @Valid @RequestBody CompartilharDocumentoRequest request
    ) {
        compartilharDocumentoService.executar(id, request.usuarioId());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/compartilhados-comigo")
    public Page<DocumentoSistemaResponse> listarCompartilhadosComigo(
        @RequestParam(required = false) String titulo,
        @RequestParam(required = false) UUID categoriaId,
        @RequestParam(required = false) String nomeArquivo,
        Pageable pageable
    ) {
        UUID usuarioId = usuarioAutenticadoPort.usuarioIdAtualObrigatorio();
        return listarDocumentosSistemaService.listarCompartilhadosComigo(
            usuarioId, titulo, categoriaId, nomeArquivo, pageable
        ).map(documento -> DocumentoSistemaResponse.de(documento, nomeCategoria(documento.categoriaId())));
    }

    private String nomeCategoria(UUID categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        return documentoCategoriaRepository.buscarPorId(categoriaId)
            .map(DocumentoCategoria::nome)
            .orElse(null);
    }
}
