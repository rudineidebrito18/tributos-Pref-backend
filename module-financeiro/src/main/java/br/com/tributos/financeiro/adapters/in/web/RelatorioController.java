package br.com.tributos.financeiro.adapters.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.financeiro.application.FiltroFaturamento;
import br.com.tributos.financeiro.application.RelatorioFaturamentoListagemService;
import br.com.tributos.financeiro.application.RelatorioFaturamentoListagemService.FaturamentoLinhaResponse;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.shared.exportacao.FormatoExportacao;

@RestController
@RequestMapping("/api/financeiro/relatorios")
public class RelatorioController {

    private final RelatorioFaturamentoListagemService relatorioFaturamentoListagemService;

    public RelatorioController(RelatorioFaturamentoListagemService relatorioFaturamentoListagemService) {
        this.relatorioFaturamentoListagemService = relatorioFaturamentoListagemService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL')")
    @GetMapping("/faturamento")
    public Object faturamento(
        @RequestParam(required = false) Boolean pago,
        @RequestParam(required = false) UUID contribuinteId,
        @RequestParam(required = false) String pagador,
        @RequestParam(required = false) String cpfcnpjpagador,
        @RequestParam(required = false) Integer mes,
        @RequestParam(required = false) Integer ano,
        @RequestParam(required = false) StatusPix statusPix,
        @RequestParam(required = false) BigDecimal valor,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEmissaoInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEmissaoFim,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamentoInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamentoFim,
        @RequestParam(required = false) String codigoConciliacaoSolicitante,
        @RequestParam(required = false) TipoTributo tipoTributo,
        @RequestParam(required = false) UUID formaPagamentoId,
        @RequestParam(required = false) FormatoExportacao formato,
        @RequestParam(required = false, defaultValue = "") String nomeTenant,
        Pageable pageable
    ) {
        FiltroFaturamento filtro = new FiltroFaturamento(
            pago,
            contribuinteId,
            pagador,
            cpfcnpjpagador,
            mes,
            ano,
            statusPix,
            valor,
            dataEmissaoInicio,
            dataEmissaoFim,
            dataPagamentoInicio,
            dataPagamentoFim,
            codigoConciliacaoSolicitante,
            tipoTributo,
            formaPagamentoId
        );

        if (formato != null) {
            return ExportacaoHttpSupport.comoAnexo(
                relatorioFaturamentoListagemService.exportar(filtro, formato, nomeTenant)
            );
        }

        Page<FaturamentoLinhaResponse> pagina = relatorioFaturamentoListagemService.listar(filtro, pageable);
        return pagina;
    }
}
