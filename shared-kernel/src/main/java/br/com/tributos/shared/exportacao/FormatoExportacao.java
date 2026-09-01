package br.com.tributos.shared.exportacao;

public enum FormatoExportacao {
    CSV("text/csv; charset=UTF-8", "csv"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    PDF("application/pdf", "pdf"),
    XML("application/xml; charset=UTF-8", "xml");

    private final String contentType;
    private final String extensao;

    FormatoExportacao(String contentType, String extensao) {
        this.contentType = contentType;
        this.extensao = extensao;
    }

    public String contentType() {
        return contentType;
    }

    public String extensao() {
        return extensao;
    }
}
