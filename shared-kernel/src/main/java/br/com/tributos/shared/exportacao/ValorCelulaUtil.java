package br.com.tributos.shared.exportacao;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

final class ValorCelulaUtil {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private ValorCelulaUtil() {
    }

    static String comoTexto(Object valor) {
        if (valor == null) {
            return "";
        }
        if (valor instanceof LocalDate ld) {
            return DATA.format(ld);
        }
        if (valor instanceof LocalDateTime ldt) {
            return DATA_HORA.format(ldt);
        }
        if (valor instanceof BigDecimal bd) {
            return bd.toPlainString();
        }
        return neutralizarFormula(valor.toString());
    }

    static String neutralizarFormula(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        char primeiro = texto.charAt(0);
        if (primeiro == '=' || primeiro == '+' || primeiro == '-' || primeiro == '@') {
            return "'" + texto;
        }
        return texto;
    }

    static String paraSnakeCase(String coluna) {
        String normalizado = coluna
            .toLowerCase()
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_+|_+$", "");
        return normalizado.isEmpty() ? "coluna" : normalizado;
    }

    static String escaparXml(String valor) {
        if (valor == null) {
            return "";
        }
        return valor
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }

    static String escaparCsv(String valor) {
        String texto = neutralizarFormula(valor);
        boolean precisaAspas = texto.contains(";") || texto.contains("\"") || texto.contains("\n") || texto.contains("\r");
        if (!precisaAspas) {
            return texto;
        }
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }

    static boolean isNumero(Object valor) {
        return valor instanceof Number;
    }

    static boolean isData(Object valor) {
        return valor instanceof LocalDate || valor instanceof LocalDateTime;
    }

    static double comoDouble(Object valor) {
        if (valor instanceof BigDecimal bd) {
            return bd.doubleValue();
        }
        if (valor instanceof Number n) {
            return n.doubleValue();
        }
        return 0;
    }

    static List<List<Object>> copiarLinhas(List<List<Object>> linhas) {
        return linhas;
    }
}
