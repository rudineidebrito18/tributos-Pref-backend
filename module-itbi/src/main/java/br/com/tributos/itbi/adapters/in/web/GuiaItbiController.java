package br.com.tributos.itbi.adapters.in.web;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.itbi.adapters.in.web.dto.AdicionarParteTransmissaoRequest;
import br.com.tributos.itbi.adapters.in.web.dto.GuiaItbiResponse;
import br.com.tributos.itbi.adapters.in.web.dto.NaturezaTransmissaoResponse;
import br.com.tributos.itbi.adapters.in.web.dto.ParteTransmissaoResponse;
import br.com.tributos.itbi.adapters.in.web.dto.SolicitarGuiaItbiRequest;
import br.com.tributos.itbi.adapters.in.web.dto.TipoGuiaItbiResponse;
import br.com.tributos.itbi.application.BuscarGuiaItbiService;
import br.com.tributos.itbi.application.ConfirmarTransferenciaTitularidadeService;
import br.com.tributos.itbi.application.GerenciarParteTransmissaoService;
import br.com.tributos.itbi.application.ListarGuiasItbiService;
import br.com.tributos.itbi.application.MontarGuiaItbiResponseService;
import br.com.tributos.itbi.application.SolicitarGuiaItbiService;
import br.com.tributos.itbi.domain.NaturezaTransmissaoRepository;
import br.com.tributos.itbi.domain.PapelParteTransmissao;
import br.com.tributos.itbi.domain.TipoGuiaItbiRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/itbi")
public class GuiaItbiController {

    private final ListarGuiasItbiService listarGuiasItbiService;
    private final BuscarGuiaItbiService buscarGuiaItbiService;
    private final SolicitarGuiaItbiService solicitarGuiaItbiService;
    private final ConfirmarTransferenciaTitularidadeService confirmarTransferenciaTitularidadeService;
    private final GerenciarParteTransmissaoService gerenciarParteTransmissaoService;
    private final TipoGuiaItbiRepository tipoGuiaItbiRepository;
    private final NaturezaTransmissaoRepository naturezaTransmissaoRepository;
    private final MontarGuiaItbiResponseService montarGuiaItbiResponseService;

    public GuiaItbiController(
        ListarGuiasItbiService listarGuiasItbiService,
        BuscarGuiaItbiService buscarGuiaItbiService,
        SolicitarGuiaItbiService solicitarGuiaItbiService,
        ConfirmarTransferenciaTitularidadeService confirmarTransferenciaTitularidadeService,
        GerenciarParteTransmissaoService gerenciarParteTransmissaoService,
        TipoGuiaItbiRepository tipoGuiaItbiRepository,
        NaturezaTransmissaoRepository naturezaTransmissaoRepository,
        MontarGuiaItbiResponseService montarGuiaItbiResponseService
    ) {
        this.listarGuiasItbiService = listarGuiasItbiService;
        this.buscarGuiaItbiService = buscarGuiaItbiService;
        this.solicitarGuiaItbiService = solicitarGuiaItbiService;
        this.confirmarTransferenciaTitularidadeService = confirmarTransferenciaTitularidadeService;
        this.gerenciarParteTransmissaoService = gerenciarParteTransmissaoService;
        this.tipoGuiaItbiRepository = tipoGuiaItbiRepository;
        this.naturezaTransmissaoRepository = naturezaTransmissaoRepository;
        this.montarGuiaItbiResponseService = montarGuiaItbiResponseService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/tipos-guia")
    public List<TipoGuiaItbiResponse> listarTiposGuia() {
        return tipoGuiaItbiRepository.listarAtivos().stream().map(TipoGuiaItbiResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/naturezas-transmissao")
    public List<NaturezaTransmissaoResponse> listarNaturezas() {
        return naturezaTransmissaoRepository.listarAtivas().stream().map(NaturezaTransmissaoResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/guias")
    public Page<GuiaItbiResponse> listar(@RequestParam(required = false) UUID imovelId, Pageable pageable) {
        return listarGuiasItbiService.executar(imovelId, pageable).map(montarGuiaItbiResponseService::montar);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/guias/{id}")
    public GuiaItbiResponse buscar(@PathVariable UUID id) {
        return montarGuiaItbiResponseService.montar(buscarGuiaItbiService.executar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @PostMapping("/guias")
    @ResponseStatus(HttpStatus.CREATED)
    public GuiaItbiResponse solicitar(@Valid @RequestBody SolicitarGuiaItbiRequest request) {
        return montarGuiaItbiResponseService.montar(solicitarGuiaItbiService.executar(
            new SolicitarGuiaItbiService.SolicitarGuiaItbiComando(
                request.imovelId(),
                request.adquirenteId(),
                request.tipoGuiaId(),
                request.naturezaTransmissaoId(),
                request.valorTransacao()
            )
        ));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/guias/{id}/partes")
    public List<ParteTransmissaoResponse> listarPartes(
        @PathVariable UUID id,
        @RequestParam PapelParteTransmissao papel
    ) {
        return gerenciarParteTransmissaoService.listar(id, papel).stream()
            .map(ParteTransmissaoResponse::de)
            .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @PostMapping("/guias/{id}/partes")
    public ResponseEntity<ParteTransmissaoResponse> adicionarParte(
        @PathVariable UUID id,
        @RequestParam PapelParteTransmissao papel,
        @Valid @RequestBody AdicionarParteTransmissaoRequest request
    ) {
        ParteTransmissaoResponse resposta = ParteTransmissaoResponse.de(
            gerenciarParteTransmissaoService.adicionar(id, new GerenciarParteTransmissaoService.AdicionarParteTransmissaoComando(
                request.contribuinteId(),
                papel,
                request.porcentagem(),
                request.principal()
            ))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @DeleteMapping("/guias/{id}/partes/{parteId}")
    public ResponseEntity<Void> removerParte(
        @PathVariable UUID id,
        @PathVariable UUID parteId
    ) {
        gerenciarParteTransmissaoService.remover(id, parteId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/guias/{id}/confirmar-transferencia")
    public GuiaItbiResponse confirmarTransferencia(@PathVariable UUID id) {
        return montarGuiaItbiResponseService.montar(confirmarTransferenciaTitularidadeService.executar(id));
    }
}
