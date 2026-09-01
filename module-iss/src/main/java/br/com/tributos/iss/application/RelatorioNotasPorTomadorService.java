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
import br.com.tributos.iss.adapters.out.persistence.RelatorioIssQueryRepository.NotasTomadorLinhaProjection;
import br.com.tributos.kernel.audit.AuditoriaPort;
import br.com.tributos.kernel.audit.RegistroAuditoria;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.shared.exportacao.ExportacaoLimiteExcedidoException;
import br.com.tributos.shared.exportacao.FormatoExportacao;
import br.com.tributos.shared.exportacao.ResultadoExportacao;
import br.com.tributos.shared.exportacao.ServicoExportacao;

@Service
public class RelatorioNotasPorTomadorService {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final List<String> COLUNAS = List.of(
        "Nº da Nota", "Contribuinte", "Atividade", "Serviço", "Data de Emissão", "Valor Serviço", "Valor ISS"
    );

    private final RelatorioIssQueryRepository queryRepository;
    private final ServicoExportacao servicoExportacao;
    private final AuditoriaPort auditoriaPort;

    public RelatorioNotasPorTomadorService(
        RelatorioIssQueryRepository queryRepository,
        ServicoExportacao servicoExportacao,
        AuditoriaPort auditoriaPort
    ) {
        this.queryRepository = queryRepository;
        this.servicoExportacao = servicoExportacao;
        this.auditoriaPort = auditoriaPort;
    }

    public Page<NotasTomadorLinhaResponse> listar(
        UUID tomadorId,
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    ) {
        validarTomador(tomadorId);
        return queryRepository.buscarNotasPorTomador(tomadorId, dataInicio, dataFim, pageable)
            .map(this::paraResponse);
    }

    public ResultadoExportacao exportar(
        UUID tomadorId,
        LocalDate dataInicio,
        LocalDate dataFim,
        FormatoExportacao formato,
        String nomeTenant
    ) {
        validarTomador(tomadorId);
        List<NotasTomadorLinhaProjection> linhas = queryRepository.buscarNotasPorTomadorTodos(
            tomadorId,
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
                l.atividade(),
                l.servico(),
                l.dataEmissao() != null ? l.dataEmissao().atZone(FUSO).toLocalDate() : null,
                l.valorServico(),
                l.valorIss()
            ))
            .toList();
        ResultadoExportacao resultado = servicoExportacao.exportar(
            formato,
            "Relatório de Atividades e Serviços Tomados",
            nomeTenant,
            COLUNAS,
            dados
        );
        auditoriaPort.registrar(new RegistroAuditoria(
            "relatorio_notas_tomador",
            tomadorId.toString(),
            "EXPORTACAO",
            java.util.Map.of("tomadorId", tomadorId, "dataInicio", dataInicio, "dataFim", dataFim),
            java.util.Map.of("linhas", linhas.size(), "formato", formato.name())
        ));
        return resultado;
    }

    private void validarTomador(UUID tomadorId) {
        if (tomadorId == null) {
            throw new ValidationException("Informe o tomador.");
        }
        if (!queryRepository.tomadorPertenceAoTenant(tomadorId)) {
            throw new NotFoundException("Tomador não encontrado.");
        }
    }

    private NotasTomadorLinhaResponse paraResponse(NotasTomadorLinhaProjection linha) {
        return new NotasTomadorLinhaResponse(
            linha.numeroNota(),
            linha.contribuinte(),
            linha.atividade(),
            linha.servico(),
            linha.dataEmissao() != null ? linha.dataEmissao().atZone(FUSO).toLocalDate() : null,
            linha.valorServico(),
            linha.valorIss()
        );
    }

    public record NotasTomadorLinhaResponse(
        long numeroNota,
        String contribuinte,
        String atividade,
        String servico,
        LocalDate dataEmissao,
        BigDecimal valorServico,
        BigDecimal valorIss
    ) {
    }
}
