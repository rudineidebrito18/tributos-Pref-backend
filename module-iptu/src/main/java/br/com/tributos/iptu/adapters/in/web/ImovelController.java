package br.com.tributos.iptu.adapters.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iptu.adapters.in.web.dto.AdicionarImovelProprietarioRequest;
import br.com.tributos.iptu.adapters.in.web.dto.CriarImovelObservacaoRequest;
import br.com.tributos.iptu.adapters.in.web.dto.ImportarImovelLegadoRequest;
import br.com.tributos.iptu.adapters.in.web.dto.ImovelObservacaoResponse;
import br.com.tributos.iptu.adapters.in.web.dto.ImovelProprietarioResponse;
import br.com.tributos.iptu.adapters.in.web.dto.ImovelResponse;
import br.com.tributos.iptu.adapters.in.web.dto.ImovelTitularidadeResponse;
import br.com.tributos.iptu.adapters.in.web.dto.SalvarImovelRequest;
import br.com.tributos.iptu.application.AdicionarImovelProprietarioComando;
import br.com.tributos.iptu.application.BuscarImovelService;
import br.com.tributos.iptu.application.GerenciarImovelObservacaoService;
import br.com.tributos.iptu.application.GerenciarImovelProprietarioService;
import br.com.tributos.iptu.application.ImportarImovelLegadoService;
import br.com.tributos.iptu.application.ListarImoveisService;
import br.com.tributos.iptu.application.ListarImovelTitularidadeService;
import br.com.tributos.iptu.application.MontarImovelResponseService;
import br.com.tributos.iptu.application.SalvarImovelComando;
import br.com.tributos.iptu.application.SalvarImovelService;

@RestController
@RequestMapping("/api/iptu/imoveis")
public class ImovelController {

    private final ListarImoveisService listarImoveisService;
    private final BuscarImovelService buscarImovelService;
    private final SalvarImovelService salvarImovelService;
    private final ImportarImovelLegadoService importarImovelLegadoService;
    private final GerenciarImovelProprietarioService gerenciarImovelProprietarioService;
    private final GerenciarImovelObservacaoService gerenciarImovelObservacaoService;
    private final ListarImovelTitularidadeService listarImovelTitularidadeService;
    private final MontarImovelResponseService montarImovelResponseService;

    public ImovelController(
        ListarImoveisService listarImoveisService,
        BuscarImovelService buscarImovelService,
        SalvarImovelService salvarImovelService,
        ImportarImovelLegadoService importarImovelLegadoService,
        GerenciarImovelProprietarioService gerenciarImovelProprietarioService,
        GerenciarImovelObservacaoService gerenciarImovelObservacaoService,
        ListarImovelTitularidadeService listarImovelTitularidadeService,
        MontarImovelResponseService montarImovelResponseService
    ) {
        this.listarImoveisService = listarImoveisService;
        this.buscarImovelService = buscarImovelService;
        this.salvarImovelService = salvarImovelService;
        this.importarImovelLegadoService = importarImovelLegadoService;
        this.gerenciarImovelProprietarioService = gerenciarImovelProprietarioService;
        this.gerenciarImovelObservacaoService = gerenciarImovelObservacaoService;
        this.listarImovelTitularidadeService = listarImovelTitularidadeService;
        this.montarImovelResponseService = montarImovelResponseService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<ImovelResponse> listar(
        @RequestParam(required = false) String busca,
        Pageable pageable
    ) {
        return listarImoveisService.executar(busca, pageable)
            .map(montarImovelResponseService::montar);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public ImovelResponse buscar(@PathVariable UUID id) {
        return montarImovelResponseService.montar(buscarImovelService.executar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<ImovelResponse> criar(@Valid @RequestBody SalvarImovelRequest request) {
        ImovelResponse resposta = montarImovelResponseService.montar(
            salvarImovelService.executar(paraComando(request), null)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PutMapping("/{id}")
    public ImovelResponse atualizar(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarImovelRequest request
    ) {
        return montarImovelResponseService.montar(salvarImovelService.executar(paraComando(request), id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping("/importar-legado")
    public List<ImovelResponse> importarLegado(@Valid @RequestBody ImportarImovelLegadoRequest request) {
        return importarImovelLegadoService.executar(
            request.itens().stream().map(ImovelController::paraComando).toList()
        ).stream().map(montarImovelResponseService::montar).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}/proprietarios")
    public List<ImovelProprietarioResponse> listarProprietarios(@PathVariable UUID id) {
        return gerenciarImovelProprietarioService.listar(id).stream()
            .map(ImovelProprietarioResponse::de)
            .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PostMapping("/{id}/proprietarios")
    public ResponseEntity<ImovelProprietarioResponse> adicionarProprietario(
        @PathVariable UUID id,
        @Valid @RequestBody AdicionarImovelProprietarioRequest request
    ) {
        ImovelProprietarioResponse resposta = ImovelProprietarioResponse.de(
            gerenciarImovelProprietarioService.adicionar(id, new AdicionarImovelProprietarioComando(
                request.contribuinteId(),
                request.porcentagem(),
                request.proprietarioPrincipal()
            ))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @DeleteMapping("/{id}/proprietarios/{proprietarioId}")
    public ResponseEntity<Void> removerProprietario(
        @PathVariable UUID id,
        @PathVariable UUID proprietarioId
    ) {
        gerenciarImovelProprietarioService.remover(id, proprietarioId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}/observacoes")
    public List<ImovelObservacaoResponse> listarObservacoes(@PathVariable UUID id) {
        return gerenciarImovelObservacaoService.listar(id).stream()
            .map(ImovelObservacaoResponse::de)
            .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @PostMapping("/{id}/observacoes")
    public ResponseEntity<ImovelObservacaoResponse> criarObservacao(
        @PathVariable UUID id,
        @Valid @RequestBody CriarImovelObservacaoRequest request
    ) {
        ImovelObservacaoResponse resposta = ImovelObservacaoResponse.de(
            gerenciarImovelObservacaoService.criar(id, request.texto())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}/titularidade")
    public List<ImovelTitularidadeResponse> listarTitularidade(@PathVariable UUID id) {
        return listarImovelTitularidadeService.executar(id).stream()
            .map(ImovelTitularidadeResponse::de)
            .toList();
    }

    private static SalvarImovelComando paraComando(SalvarImovelRequest request) {
        return new SalvarImovelComando(
            request.codigoLegado(),
            request.proprietarioId(),
            request.tipoId(),
            request.enderecoId(),
            request.areaTerreno(),
            request.areaConstruida(),
            request.destinacaoId(),
            request.tipoEdificacaoId(),
            request.tipoLimitacaoId(),
            request.zonaFiscalId(),
            request.valorVenalTerreno(),
            request.valorVenalConstrucao(),
            request.situacao(),
            request.anoExercicio(),
            request.dataInclusao(),
            request.areaTotal(),
            request.frente(),
            request.fundos(),
            request.ladoEsquerdo(),
            request.ladoDireito(),
            request.quadra(),
            request.lote(),
            request.loteamento(),
            request.edificio(),
            request.bloco(),
            request.sala(),
            request.apartamento(),
            request.bairroIptuId(),
            request.logradouroIptuId(),
            request.valorVenalUnidade(),
            request.valorAvaliacao(),
            request.enderecoCorrespondenciaId(),
            request.observacao()
        );
    }
}
