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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.iptu.adapters.in.web.dto.GerarLancamentosRequest;
import br.com.tributos.iptu.adapters.in.web.dto.LancamentoIptuDetalheResponse;
import br.com.tributos.iptu.adapters.in.web.dto.LancamentoIptuResponse;
import br.com.tributos.iptu.application.BuscarLancamentoService;
import br.com.tributos.iptu.application.GerarLancamentoAnualService;
import br.com.tributos.iptu.application.ListarLancamentosService;

@RestController
@RequestMapping("/api/iptu")
public class LancamentoController {

    private final ListarLancamentosService listarLancamentosService;
    private final BuscarLancamentoService buscarLancamentoService;
    private final GerarLancamentoAnualService gerarLancamentoAnualService;

    public LancamentoController(
        ListarLancamentosService listarLancamentosService,
        BuscarLancamentoService buscarLancamentoService,
        GerarLancamentoAnualService gerarLancamentoAnualService
    ) {
        this.listarLancamentosService = listarLancamentosService;
        this.buscarLancamentoService = buscarLancamentoService;
        this.gerarLancamentoAnualService = gerarLancamentoAnualService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/lancamentos")
    public Page<LancamentoIptuResponse> listar(
        @RequestParam(required = false) Integer exercicio,
        @RequestParam(required = false) UUID imovelId,
        Pageable pageable
    ) {
        return listarLancamentosService.executar(exercicio, imovelId, pageable).map(LancamentoIptuResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/lancamentos/{id}")
    public LancamentoIptuDetalheResponse buscar(@PathVariable UUID id) {
        return LancamentoIptuDetalheResponse.de(buscarLancamentoService.executar(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/exercicios/{exercicio}/gerar-lancamentos")
    public ResponseEntity<List<LancamentoIptuResponse>> gerarLancamentos(
        @PathVariable int exercicio,
        @RequestBody(required = false) GerarLancamentosRequest request
    ) {
        Integer numeroParcelas = request != null ? request.numeroParcelas() : null;
        List<LancamentoIptuResponse> resposta = gerarLancamentoAnualService.executar(exercicio, numeroParcelas)
            .stream()
            .map(LancamentoIptuResponse::de)
            .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
