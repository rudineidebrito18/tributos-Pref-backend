package br.com.tributos.iss.adapters.in.web;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.iss.application.RelatorioIrpfService;
import br.com.tributos.iss.application.RelatorioIrpfService.RelatorioIrpfResponse;
import br.com.tributos.iss.application.RelatorioNotasPorTomadorService;
import br.com.tributos.shared.exportacao.FormatoExportacao;

@RestController
@RequestMapping("/api/iss/relatorios")
public class RelatorioIssController {

    private final RelatorioIrpfService relatorioIrpfService;
    private final RelatorioNotasPorTomadorService relatorioNotasPorTomadorService;

    public RelatorioIssController(
        RelatorioIrpfService relatorioIrpfService,
        RelatorioNotasPorTomadorService relatorioNotasPorTomadorService
    ) {
        this.relatorioIrpfService = relatorioIrpfService;
        this.relatorioNotasPorTomadorService = relatorioNotasPorTomadorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @GetMapping("/irpf")
    public Object irpf(
        @RequestParam(required = false) UUID contribuinteId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) FormatoExportacao formato,
        @RequestParam(required = false, defaultValue = "") String nomeTenant,
        Pageable pageable
    ) {
        if (formato != null) {
            return ExportacaoHttpSupport.comoAnexo(
                relatorioIrpfService.exportar(contribuinteId, dataInicio, dataFim, formato, nomeTenant)
            );
        }
        RelatorioIrpfResponse resposta = relatorioIrpfService.listar(contribuinteId, dataInicio, dataFim, pageable);
        return resposta;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @GetMapping("/notas-tomador")
    public Object notasPorTomador(
        @RequestParam UUID tomadorId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) FormatoExportacao formato,
        @RequestParam(required = false, defaultValue = "") String nomeTenant,
        Pageable pageable
    ) {
        if (formato != null) {
            return ExportacaoHttpSupport.comoAnexo(
                relatorioNotasPorTomadorService.exportar(tomadorId, dataInicio, dataFim, formato, nomeTenant)
            );
        }
        return relatorioNotasPorTomadorService.listar(tomadorId, dataInicio, dataFim, pageable);
    }
}
