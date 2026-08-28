package br.com.tributos.iptu.adapters.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iptu.adapters.in.web.dto.ImportarImovelLegadoRequest;
import br.com.tributos.iptu.adapters.in.web.dto.ImovelResponse;
import br.com.tributos.iptu.adapters.in.web.dto.SalvarImovelRequest;
import br.com.tributos.iptu.application.BuscarImovelService;
import br.com.tributos.iptu.application.ImportarImovelLegadoService;
import br.com.tributos.iptu.application.ListarImoveisService;
import br.com.tributos.iptu.application.SalvarImovelComando;
import br.com.tributos.iptu.application.SalvarImovelService;

@RestController
@RequestMapping("/api/iptu/imoveis")
public class ImovelController {

    private final ListarImoveisService listarImoveisService;
    private final BuscarImovelService buscarImovelService;
    private final SalvarImovelService salvarImovelService;
    private final ImportarImovelLegadoService importarImovelLegadoService;

    public ImovelController(
        ListarImoveisService listarImoveisService,
        BuscarImovelService buscarImovelService,
        SalvarImovelService salvarImovelService,
        ImportarImovelLegadoService importarImovelLegadoService
    ) {
        this.listarImoveisService = listarImoveisService;
        this.buscarImovelService = buscarImovelService;
        this.salvarImovelService = salvarImovelService;
        this.importarImovelLegadoService = importarImovelLegadoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<ImovelResponse> listar(
        @RequestParam(required = false) String busca,
        Pageable pageable
    ) {
        return listarImoveisService.executar(busca, pageable).map(ImovelResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public ImovelResponse buscar(@PathVariable UUID id) {
        return ImovelResponse.de(buscarImovelService.executar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<ImovelResponse> criar(@Valid @RequestBody SalvarImovelRequest request) {
        ImovelResponse resposta = ImovelResponse.de(salvarImovelService.executar(paraComando(request), null));
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PutMapping("/{id}")
    public ImovelResponse atualizar(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarImovelRequest request
    ) {
        return ImovelResponse.de(salvarImovelService.executar(paraComando(request), id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping("/importar-legado")
    public List<ImovelResponse> importarLegado(@Valid @RequestBody ImportarImovelLegadoRequest request) {
        return importarImovelLegadoService.executar(
            request.itens().stream().map(ImovelController::paraComando).toList()
        ).stream().map(ImovelResponse::de).toList();
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
            request.situacao()
        );
    }
}
