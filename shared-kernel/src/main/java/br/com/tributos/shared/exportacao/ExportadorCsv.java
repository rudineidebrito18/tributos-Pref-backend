package br.com.tributos.shared.exportacao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExportadorCsv implements ExportadorRelatorio {

    private static final byte[] BOM = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    @Override
    public FormatoExportacao formato() {
        return FormatoExportacao.CSV;
    }

    @Override
    public byte[] exportar(String titulo, String nomeTenant, List<String> colunas, List<List<Object>> linhas) {
        try (var out = new ByteArrayOutputStream()) {
            out.write(BOM);
            out.write(linha(colunas).getBytes(StandardCharsets.UTF_8));
            for (List<Object> linha : linhas) {
                out.write(linha(linha.stream().map(ValorCelulaUtil::comoTexto).toList()).getBytes(StandardCharsets.UTF_8));
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao gerar CSV", e);
        }
    }

    private static String linha(List<String> valores) {
        return String.join(";", valores.stream().map(ValorCelulaUtil::escaparCsv).toList()) + "\r\n";
    }
}
