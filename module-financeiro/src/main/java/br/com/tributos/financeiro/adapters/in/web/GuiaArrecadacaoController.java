package br.com.tributos.financeiro.adapters.in.web;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.financeiro.adapters.in.web.dto.BaixaManualRequest;
import br.com.tributos.financeiro.adapters.in.web.dto.EmitirDamAvulsoRequest;
import br.com.tributos.financeiro.adapters.in.web.dto.GuiaArrecadacaoResponse;
import br.com.tributos.financeiro.adapters.in.web.dto.SimulacaoPixResponse;
import br.com.tributos.financeiro.application.BuscarGuiaService;
import br.com.tributos.financeiro.application.EmitirDamAvulsoService;
import br.com.tributos.financeiro.application.ListarGuiasService;
import br.com.tributos.financeiro.application.RegistrarPagamentoService;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributo;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/financeiro/guias-arrecadacao")
public class GuiaArrecadacaoController {

    private final ListarGuiasService listarGuiasService;
    private final BuscarGuiaService buscarGuiaService;
    private final EmitirDamAvulsoService emitirDamAvulsoService;
    private final RegistrarPagamentoService registrarPagamentoService;
    private final GuiaArrecadacaoResponseMapper guiaArrecadacaoResponseMapper;

    public GuiaArrecadacaoController(
        ListarGuiasService listarGuiasService,
        BuscarGuiaService buscarGuiaService,
        EmitirDamAvulsoService emitirDamAvulsoService,
        RegistrarPagamentoService registrarPagamentoService,
        GuiaArrecadacaoResponseMapper guiaArrecadacaoResponseMapper
    ) {
        this.listarGuiasService = listarGuiasService;
        this.buscarGuiaService = buscarGuiaService;
        this.emitirDamAvulsoService = emitirDamAvulsoService;
        this.registrarPagamentoService = registrarPagamentoService;
        this.guiaArrecadacaoResponseMapper = guiaArrecadacaoResponseMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<GuiaArrecadacaoResponse> listar(
        @RequestParam(required = false) TipoTributo tipoTributo,
        @RequestParam(required = false) SituacaoGuia situacao,
        @RequestParam(required = false) UUID contribuinteId,
        @RequestParam(required = false) StatusPix statusPix,
        @RequestParam(required = false) String formaPagamentoCodigo,
        Pageable pageable
    ) {
        return listarGuiasService.executar(
            tipoTributo, situacao, contribuinteId, statusPix, formaPagamentoCodigo, pageable
        ).map(guiaArrecadacaoResponseMapper::paraResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public GuiaArrecadacaoResponse buscar(@PathVariable UUID id) {
        return guiaArrecadacaoResponseMapper.paraResponse(buscarGuiaService.executar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @PostMapping("/avulso")
    @ResponseStatus(HttpStatus.CREATED)
    public GuiaArrecadacaoResponse emitirAvulso(@Valid @RequestBody EmitirDamAvulsoRequest request) {
        return guiaArrecadacaoResponseMapper.paraResponse(emitirDamAvulsoService.executar(
            request.contribuinteId(),
            request.valor(),
            request.dataVencimento(),
            request.descricao()
        ));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @PostMapping("/{id}/simular-pix")
    public SimulacaoPixResponse simularPix(@PathVariable UUID id) {
        var r = registrarPagamentoService.simularPix(id);
        return new SimulacaoPixResponse(r.pixTxid(), r.codigoBarras(), r.qrCodePayload());
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @PostMapping("/{id}/confirmar-pix")
    public GuiaArrecadacaoResponse confirmarPix(@PathVariable UUID id) {
        return guiaArrecadacaoResponseMapper.paraResponse(registrarPagamentoService.confirmarPix(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/{id}/baixa-manual")
    public GuiaArrecadacaoResponse baixaManual(@PathVariable UUID id, @Valid @RequestBody BaixaManualRequest request) {
        return guiaArrecadacaoResponseMapper.paraResponse(registrarPagamentoService.baixaManual(
            id, request.valorPago()
        ));
    }
}
