package br.com.tributos.iptu.adapters.in.web;

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
import br.com.tributos.iptu.adapters.in.web.dto.DestinacaoImovelResponse;
import br.com.tributos.iptu.adapters.in.web.dto.HabiteseTipoResponse;
import br.com.tributos.iptu.adapters.in.web.dto.SalvarDestinacaoImovelRequest;
import br.com.tributos.iptu.adapters.in.web.dto.SalvarHabiteseTipoRequest;
import br.com.tributos.iptu.application.GerenciarDestinacaoImovelService;
import br.com.tributos.iptu.application.GerenciarHabiteseTipoService;

@RestController
@RequestMapping("/api/iptu/apoio")
public class CatalogoIptuApoioController {

    private final GerenciarDestinacaoImovelService gerenciarDestinacaoImovelService;
    private final GerenciarHabiteseTipoService gerenciarHabiteseTipoService;

    public CatalogoIptuApoioController(
        GerenciarDestinacaoImovelService gerenciarDestinacaoImovelService,
        GerenciarHabiteseTipoService gerenciarHabiteseTipoService
    ) {
        this.gerenciarDestinacaoImovelService = gerenciarDestinacaoImovelService;
        this.gerenciarHabiteseTipoService = gerenciarHabiteseTipoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/destinacoes")
    public List<DestinacaoImovelResponse> listarDestinacoes() {
        return gerenciarDestinacaoImovelService.listar().stream().map(DestinacaoImovelResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/destinacoes/{id}")
    public DestinacaoImovelResponse buscarDestinacao(@PathVariable UUID id) {
        return DestinacaoImovelResponse.de(gerenciarDestinacaoImovelService.buscar(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/destinacoes")
    public ResponseEntity<DestinacaoImovelResponse> criarDestinacao(
        @Valid @RequestBody SalvarDestinacaoImovelRequest request
    ) {
        DestinacaoImovelResponse resposta = DestinacaoImovelResponse.de(
            gerenciarDestinacaoImovelService.criar(
                request.nome(), request.ativo(), request.tipoImovelId(), request.aliquotaIptu()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/destinacoes/{id}")
    public DestinacaoImovelResponse atualizarDestinacao(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarDestinacaoImovelRequest request
    ) {
        return DestinacaoImovelResponse.de(
            gerenciarDestinacaoImovelService.atualizar(
                id, request.nome(), request.ativo(), request.tipoImovelId(), request.aliquotaIptu()
            )
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/destinacoes/{id}")
    public ResponseEntity<Void> excluirDestinacao(@PathVariable UUID id) {
        gerenciarDestinacaoImovelService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/habitese-tipos")
    public List<HabiteseTipoResponse> listarHabiteseTipos() {
        return gerenciarHabiteseTipoService.listar().stream().map(HabiteseTipoResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/habitese-tipos/{id}")
    public HabiteseTipoResponse buscarHabiteseTipo(@PathVariable UUID id) {
        return HabiteseTipoResponse.de(gerenciarHabiteseTipoService.buscar(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/habitese-tipos")
    public ResponseEntity<HabiteseTipoResponse> criarHabiteseTipo(
        @Valid @RequestBody SalvarHabiteseTipoRequest request
    ) {
        HabiteseTipoResponse resposta = HabiteseTipoResponse.de(
            gerenciarHabiteseTipoService.criar(
                request.nome(), request.ativo(), request.titulo(), request.permiteDesconto(),
                request.habilitaCalculoValor(), request.valor(), request.secretaria(), request.cargo()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/habitese-tipos/{id}")
    public HabiteseTipoResponse atualizarHabiteseTipo(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarHabiteseTipoRequest request
    ) {
        return HabiteseTipoResponse.de(
            gerenciarHabiteseTipoService.atualizar(
                id, request.nome(), request.ativo(), request.titulo(), request.permiteDesconto(),
                request.habilitaCalculoValor(), request.valor(), request.secretaria(), request.cargo()
            )
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/habitese-tipos/{id}")
    public ResponseEntity<Void> excluirHabiteseTipo(@PathVariable UUID id) {
        gerenciarHabiteseTipoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping(value = "/habitese-tipos/{id}/assinatura", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HabiteseTipoResponse anexarAssinatura(
        @PathVariable UUID id,
        @RequestPart("arquivo") MultipartFile arquivo
    ) {
        return HabiteseTipoResponse.de(gerenciarHabiteseTipoService.anexarAssinatura(id, arquivo));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/habitese-tipos/{id}/assinatura/download")
    public ResponseEntity<org.springframework.core.io.Resource> baixarAssinatura(@PathVariable UUID id) {
        BaixarDocumentoInstitucionalService.ArquivoParaDownload arquivo =
            gerenciarHabiteseTipoService.baixarAssinatura(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
            ContentDisposition.attachment().filename(arquivo.nomeArquivo()).build()
        );
        headers.setContentType(arquivo.conteudoTipo());
        return ResponseEntity.ok().headers(headers).body(arquivo.recurso());
    }
}
