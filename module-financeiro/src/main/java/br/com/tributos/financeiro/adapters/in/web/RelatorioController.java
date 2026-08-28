package br.com.tributos.financeiro.adapters.in.web;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.financeiro.application.RelatorioFaturamentoService;
import br.com.tributos.financeiro.domain.TipoTributo;

@RestController
@RequestMapping("/api/financeiro/relatorios")
public class RelatorioController {

    private final RelatorioFaturamentoService relatorioFaturamentoService;

    public RelatorioController(RelatorioFaturamentoService relatorioFaturamentoService) {
        this.relatorioFaturamentoService = relatorioFaturamentoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @GetMapping("/faturamento")
    public RelatorioFaturamentoService.RelatorioFaturamentoResult faturamento(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
        @RequestParam(required = false) TipoTributo tipoTributo
    ) {
        return relatorioFaturamentoService.executar(de, ate, tipoTributo);
    }
}
