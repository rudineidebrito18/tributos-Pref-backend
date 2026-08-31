package br.com.tributos.itbi.adapters.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import br.com.tributos.cadastro.application.BaixarDocumentoInstitucionalService;
import br.com.tributos.itbi.adapters.in.web.dto.SalvarTipoCalculoGuiaItbiRequest;
import br.com.tributos.itbi.adapters.in.web.dto.SalvarTipoGuiaItbiCatalogoRequest;
import br.com.tributos.itbi.adapters.in.web.dto.TipoCalculoGuiaItbiResponse;
import br.com.tributos.itbi.adapters.in.web.dto.TipoGuiaItbiCatalogoResponse;
import br.com.tributos.itbi.application.GerenciarCatalogoItbiService;

@RestController
@RequestMapping("/api/itbi/catalogo")
public class CatalogoItbiController {

    private final GerenciarCatalogoItbiService gerenciarCatalogoItbiService;

    public CatalogoItbiController(GerenciarCatalogoItbiService gerenciarCatalogoItbiService) {
        this.gerenciarCatalogoItbiService = gerenciarCatalogoItbiService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/tipos-calculo")
    public List<TipoCalculoGuiaItbiResponse> listarTiposCalculo() {
        return gerenciarCatalogoItbiService.listarTiposCalculo().stream()
            .map(TipoCalculoGuiaItbiResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/tipos-calculo/{id}")
    public TipoCalculoGuiaItbiResponse buscarTipoCalculo(@PathVariable UUID id) {
        return TipoCalculoGuiaItbiResponse.de(gerenciarCatalogoItbiService.buscarTipoCalculo(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/tipos-calculo")
    public ResponseEntity<TipoCalculoGuiaItbiResponse> criarTipoCalculo(
        @Valid @RequestBody SalvarTipoCalculoGuiaItbiRequest request
    ) {
        TipoCalculoGuiaItbiResponse resposta = TipoCalculoGuiaItbiResponse.de(
            gerenciarCatalogoItbiService.criarTipoCalculo(request.descricao())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/tipos-calculo/{id}")
    public TipoCalculoGuiaItbiResponse atualizarTipoCalculo(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarTipoCalculoGuiaItbiRequest request
    ) {
        return TipoCalculoGuiaItbiResponse.de(
            gerenciarCatalogoItbiService.atualizarTipoCalculo(id, request.descricao())
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/tipos-calculo/{id}")
    public ResponseEntity<Void> excluirTipoCalculo(@PathVariable UUID id) {
        gerenciarCatalogoItbiService.excluirTipoCalculo(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/tipos-guia")
    public List<TipoGuiaItbiCatalogoResponse> listarTiposGuia() {
        return gerenciarCatalogoItbiService.listarTiposGuia().stream()
            .map(TipoGuiaItbiCatalogoResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/tipos-guia/{id}")
    public TipoGuiaItbiCatalogoResponse buscarTipoGuia(@PathVariable UUID id) {
        return TipoGuiaItbiCatalogoResponse.de(gerenciarCatalogoItbiService.buscarTipoGuia(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/tipos-guia")
    public ResponseEntity<TipoGuiaItbiCatalogoResponse> criarTipoGuia(
        @Valid @RequestBody SalvarTipoGuiaItbiCatalogoRequest request
    ) {
        TipoGuiaItbiCatalogoResponse resposta = TipoGuiaItbiCatalogoResponse.de(
            gerenciarCatalogoItbiService.criarTipoGuia(
                request.nome(), request.aliquota(), request.ativo(), request.tipoCalculoId(),
                request.permiteDesconto(), request.habilitaCalculoValor(), request.valor(),
                request.valorParcela(), request.secretaria(), request.cargo()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/tipos-guia/{id}")
    public TipoGuiaItbiCatalogoResponse atualizarTipoGuia(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarTipoGuiaItbiCatalogoRequest request
    ) {
        return TipoGuiaItbiCatalogoResponse.de(
            gerenciarCatalogoItbiService.atualizarTipoGuia(
                id, request.nome(), request.aliquota(), request.ativo(), request.tipoCalculoId(),
                request.permiteDesconto(), request.habilitaCalculoValor(), request.valor(),
                request.valorParcela(), request.secretaria(), request.cargo()
            )
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/tipos-guia/{id}")
    public ResponseEntity<Void> excluirTipoGuia(@PathVariable UUID id) {
        gerenciarCatalogoItbiService.excluirTipoGuia(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping(value = "/tipos-guia/{id}/assinatura", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TipoGuiaItbiCatalogoResponse anexarAssinatura(
        @PathVariable UUID id,
        @RequestPart("arquivo") MultipartFile arquivo
    ) {
        return TipoGuiaItbiCatalogoResponse.de(
            gerenciarCatalogoItbiService.anexarAssinaturaTipoGuia(id, arquivo)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/tipos-guia/{id}/assinatura/download")
    public ResponseEntity<org.springframework.core.io.Resource> baixarAssinatura(@PathVariable UUID id) {
        BaixarDocumentoInstitucionalService.ArquivoParaDownload arquivo =
            gerenciarCatalogoItbiService.baixarAssinaturaTipoGuia(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
            ContentDisposition.attachment().filename(arquivo.nomeArquivo()).build()
        );
        headers.setContentType(arquivo.conteudoTipo());
        return ResponseEntity.ok().headers(headers).body(arquivo.recurso());
    }
}
