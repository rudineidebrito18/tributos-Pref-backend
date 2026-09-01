package br.com.tributos.shared.exportacao;

import java.util.List;

public interface ExportadorRelatorio {

    FormatoExportacao formato();

    byte[] exportar(String titulo, String nomeTenant, List<String> colunas, List<List<Object>> linhas);
}
