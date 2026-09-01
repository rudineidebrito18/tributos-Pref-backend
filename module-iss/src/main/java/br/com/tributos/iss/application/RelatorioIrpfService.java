package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.tributos.iss.adapters.out.persistence.RelatorioIssQueryRepository;
import br.com.tributos.iss.adapters.out.persistence.RelatorioIssQueryRepository.IrpfLinhaProjection;
import br.com.tributos.kernel.audit.AuditoriaPort;
import br.com.tributos.kernel.audit.RegistroAuditoria;
import br.com.tributos.shared.exportacao.ExportacaoLimiteExcedidoException;
import br.com.tributos.shared.exportacao.FormatoExportacao;
import br.com.tributos.shared.exportacao.ResultadoExportacao;
import br.com.tributos.shared.exportacao.ServicoExportacao;

@Service
public class RelatorioIrpfService {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final List<String> COLUNAS = List.of(
        "Nº da Nota", "Contribuinte", "Data de Emissão", "Valor IR"
    );

    private final RelatorioIssQueryRepository queryRepository;
    private final ServicoExportacao servicoExportacao;
    private final AuditoriaPort auditoriaPort;

    public RelatorioIrpfService(
        RelatorioIssQueryRepository queryRepository,
        ServicoExportacao servicoExportacao,
        AuditoriaPort auditoriaPort
    ) {
        this.queryRepository = queryRepository;
        this.servicoExportacao = servicoExportacao;
        this.auditoriaPort = auditoriaPort;
    }

    public RelatorioIrpfResponse listar(UUID contribuinteId, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        Page<IrpfLinhaResponse> pagina = queryRepository.buscarIrpf(contribuinteId, dataInicio, dataFim, pageable)
            .map(this::paraResponse);
        BigDecimal total = queryRepository.somarIrpf(contribuinteId, dataInicio, dataFim);
        return new RelatorioIrpfResponse(pagina, total);
    }

    public ResultadoExportacao exportar(
        UUID contribuinteId,
        LocalDate dataInicio,
        LocalDate dataFim,
        FormatoExportacao formato,
        String nomeTenant
    ) {
        List<IrpfLinhaProjection> linhas = queryRepository.buscarIrpfTodos(
            contribuinteId,
            dataInicio,
            dataFim,
            ExportacaoLimiteExcedidoException.LIMITE_LINHAS + 1
        );
        if (linhas.size() > ExportacaoLimiteExcedidoException.LIMITE_LINHAS) {
            throw new ExportacaoLimiteExcedidoException();
        }
        List<List<Object>> dados = linhas.stream()
            .map(l -> List.<Object>of(
                l.numeroNota(),
                l.contribuinte(),
                l.dataEmissao() != null ? l.dataEmissao().atZone(FUSO).toLocalDate() : null,
                l.valorIr()
            ))
            .toList();
        ResultadoExportacao resultado = servicoExportacao.exportar(
            formato,
            "Relatório IRPF por Nota",
            nomeTenant,
            COLUNAS,
            dados
        );
        auditoriaPort.registrar(new RegistroAuditoria(
            "relatorio_irpf",
            formato.name(),
            "EXPORTACAO",
            java.util.Map.of("contribuinteId", contribuinteId, "dataInicio", dataInicio, "dataFim", dataFim),
            java.util.Map.of("linhas", linhas.size(), "formato", formato.name())
        ));
        return resultado;
    }

    private IrpfLinhaResponse paraResponse(IrpfLinhaProjection linha) {
        return new IrpfLinhaResponse(
            linha.numeroNota(),
            linha.contribuinte(),
            linha.dataEmissao() != null ? linha.dataEmissao().atZone(FUSO).toLocalDate() : null,
            linha.valorIr()
        );
    }

    public record IrpfLinhaResponse(long numeroNota, String contribuinte, LocalDate dataEmissao, BigDecimal valorIr) {
    }

    public record RelatorioIrpfResponse(Page<IrpfLinhaResponse> conteudo, BigDecimal totalValorIr) {
    }
}
