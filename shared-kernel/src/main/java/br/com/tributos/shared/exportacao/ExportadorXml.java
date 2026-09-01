package br.com.tributos.shared.exportacao;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExportadorXml implements ExportadorRelatorio {

    @Override
    public FormatoExportacao formato() {
        return FormatoExportacao.XML;
    }

    @Override
    public byte[] exportar(String titulo, String nomeTenant, List<String> colunas, List<List<Object>> linhas) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<relatorio");
        if (titulo != null && !titulo.isBlank()) {
            xml.append(" titulo=\"").append(ValorCelulaUtil.escaparXml(titulo)).append("\"");
        }
        if (nomeTenant != null && !nomeTenant.isBlank()) {
            xml.append(" tenant=\"").append(ValorCelulaUtil.escaparXml(nomeTenant)).append("\"");
        }
        xml.append(">\n");

        for (List<Object> linha : linhas) {
            xml.append("  <linha>\n");
            for (int i = 0; i < colunas.size(); i++) {
                String tag = ValorCelulaUtil.paraSnakeCase(colunas.get(i));
                Object valor = i < linha.size() ? linha.get(i) : null;
                xml.append("    <").append(tag).append(">")
                    .append(ValorCelulaUtil.escaparXml(ValorCelulaUtil.comoTexto(valor)))
                    .append("</").append(tag).append(">\n");
            }
            xml.append("  </linha>\n");
        }

        xml.append("</relatorio>\n");
        return xml.toString().getBytes(StandardCharsets.UTF_8);
    }
}
