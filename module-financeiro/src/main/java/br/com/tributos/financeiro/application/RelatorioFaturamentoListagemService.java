package br.com.tributos.financeiro.application;

import java.time.ZoneId;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.tributos.financeiro.adapters.out.persistence.FaturamentoRelatorioQueryRepository;
import br.com.tributos.financeiro.adapters.out.persistence.FaturamentoRelatorioQueryRepository.FaturamentoLinhaProjection;
import br.com.tributos.kernel.audit.AuditoriaPort;
import br.com.tributos.kernel.audit.RegistroAuditoria;
import br.com.tributos.shared.exportacao.ExportacaoLimiteExcedidoException;
import br.com.tributos.shared.exportacao.FormatoExportacao;
import br.com.tributos.shared.exportacao.ResultadoExportacao;
import br.com.tributos.shared.exportacao.ServicoExportacao;

@Service
public class RelatorioFaturamentoListagemService {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final List<String> COLUNAS_EXPORTACAO = List.of(
        "MES/ANO/VERSÃO", "CPF/CNPJ", "CONTRIBUINTE", "SITUAÇÃO",
        "FORMA DE PAGAMENTO", "TIPO DE TRIBUTO", "EMISSÃO", "EFETIVAÇÃO", "VALOR", "VALOR PAGO"
    );

    private final FaturamentoRelatorioQueryRepository queryRepository;
    private final ServicoExportacao servicoExportacao;
    private final AuditoriaPort auditoriaPort;

    public RelatorioFaturamentoListagemService(
        FaturamentoRelatorioQueryRepository queryRepository,
        ServicoExportacao servicoExportacao,
        AuditoriaPort auditoriaPort
    ) {
        this.queryRepository = queryRepository;
        this.servicoExportacao = servicoExportacao;
        this.auditoriaPort = auditoriaPort;
    }

    public Page<FaturamentoLinhaResponse> listar(FiltroFaturamento filtro, Pageable pageable) {
        return queryRepository.buscar(filtro, pageable).map(this::paraResponse);
    }

    public ResultadoExportacao exportar(FiltroFaturamento filtro, FormatoExportacao formato, String nomeTenant) {
        List<FaturamentoLinhaProjection> linhas = queryRepository.buscarTodos(
            filtro,
            ExportacaoLimiteExcedidoException.LIMITE_LINHAS + 1
        );
        if (linhas.size() > ExportacaoLimiteExcedidoException.LIMITE_LINHAS) {
            throw new ExportacaoLimiteExcedidoException();
        }
        List<List<Object>> dados = linhas.stream().map(this::paraLinhaExportacao).toList();
        ResultadoExportacao resultado = servicoExportacao.exportar(
            formato,
            "Relatório de Faturamento",
            nomeTenant,
            COLUNAS_EXPORTACAO,
            dados
        );
        auditoriaPort.registrar(new RegistroAuditoria(
            "relatorio_faturamento",
            formato.name(),
            "EXPORTACAO",
            filtro,
            java.util.Map.of("linhas", linhas.size(), "formato", formato.name())
        ));
        return resultado;
    }

    private FaturamentoLinhaResponse paraResponse(FaturamentoLinhaProjection linha) {
        return new FaturamentoLinhaResponse(
            linha.guiaId(),
            linha.mesAnoVersao(),
            linha.cpfCnpj(),
            linha.contribuinte(),
            linha.situacao().name(),
            linha.formaPagamento(),
            linha.tipoTributo(),
            linha.emissao() != null ? linha.emissao().atZone(FUSO).toLocalDate() : null,
            linha.efetivacao() != null ? linha.efetivacao().atZone(FUSO).toLocalDate() : null,
            linha.valor(),
            linha.valorPago()
        );
    }

    private List<Object> paraLinhaExportacao(FaturamentoLinhaProjection linha) {
        return List.of(
            linha.mesAnoVersao(),
            linha.cpfCnpj(),
            linha.contribuinte(),
            linha.situacao().name(),
            linha.formaPagamento() != null ? linha.formaPagamento() : "",
            linha.tipoTributo(),
            linha.emissao() != null ? linha.emissao().atZone(FUSO).toLocalDate() : null,
            linha.efetivacao() != null ? linha.efetivacao().atZone(FUSO).toLocalDate() : null,
            linha.valor(),
            linha.valorPago() != null ? linha.valorPago() : linha.valor()
        );
    }

    public record FaturamentoLinhaResponse(
        java.util.UUID guiaId,
        String mesAnoVersao,
        String cpfCnpj,
        String contribuinte,
        String situacao,
        String formaPagamento,
        String tipoTributo,
        java.time.LocalDate emissao,
        java.time.LocalDate efetivacao,
        java.math.BigDecimal valor,
        java.math.BigDecimal valorPago
    ) {
    }
}
