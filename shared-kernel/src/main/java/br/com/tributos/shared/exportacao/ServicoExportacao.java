package br.com.tributos.shared.exportacao;

import java.text.Normalizer;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServicoExportacao {

    private final Map<FormatoExportacao, ExportadorRelatorio> exportadores;

    public ServicoExportacao() {
        this(List.of(
            new ExportadorCsv(),
            new ExportadorXlsx(),
            new ExportadorPdf(),
            new ExportadorXml()
        ));
    }

    ServicoExportacao(List<ExportadorRelatorio> implementacoes) {
        exportadores = new EnumMap<>(FormatoExportacao.class);
        for (ExportadorRelatorio exportador : implementacoes) {
            exportadores.put(exportador.formato(), exportador);
        }
    }

    public ResultadoExportacao exportar(
        FormatoExportacao formato,
        String titulo,
        String nomeTenant,
        List<String> colunas,
        List<List<Object>> linhas
    ) {
        if (linhas.size() > ExportacaoLimiteExcedidoException.LIMITE_LINHAS) {
            throw new ExportacaoLimiteExcedidoException();
        }
        ExportadorRelatorio exportador = exportadores.get(formato);
        if (exportador == null) {
            throw new IllegalArgumentException("Formato de exportação não suportado: " + formato);
        }
        byte[] conteudo = exportador.exportar(titulo, nomeTenant, colunas, linhas);
        String nomeArquivo = sanitizarNomeArquivo(titulo) + "." + formato.extensao();
        return new ResultadoExportacao(conteudo, formato.contentType(), nomeArquivo);
    }

    private static String sanitizarNomeArquivo(String titulo) {
        String base = titulo == null || titulo.isBlank() ? "relatorio" : titulo;
        String semAcento = Normalizer.normalize(base, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        String limpo = semAcento.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
        return limpo.isEmpty() ? "relatorio" : limpo;
    }
}
