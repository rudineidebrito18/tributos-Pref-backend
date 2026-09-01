package br.com.tributos.shared.exportacao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportadorXlsx implements ExportadorRelatorio {

    @Override
    public FormatoExportacao formato() {
        return FormatoExportacao.XLSX;
    }

    @Override
    public byte[] exportar(String titulo, String nomeTenant, List<String> colunas, List<List<Object>> linhas) {
        try (var workbook = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sanitizarNomeAba(titulo));
            CreationHelper helper = workbook.getCreationHelper();

            CellStyle cabecalhoStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            cabecalhoStyle.setFont(font);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setDataFormat(helper.createDataFormat().getFormat("dd/mm/yyyy"));

            CellStyle dataHoraStyle = workbook.createCellStyle();
            dataHoraStyle.setDataFormat(helper.createDataFormat().getFormat("dd/mm/yyyy hh:mm:ss"));

            CellStyle numeroStyle = workbook.createCellStyle();
            numeroStyle.setDataFormat(helper.createDataFormat().getFormat("#,##0.00"));

            Row header = sheet.createRow(0);
            for (int c = 0; c < colunas.size(); c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(colunas.get(c));
                cell.setCellStyle(cabecalhoStyle);
            }

            int rowIdx = 1;
            for (List<Object> linha : linhas) {
                Row row = sheet.createRow(rowIdx++);
                for (int c = 0; c < linha.size(); c++) {
                    preencherCelula(row.createCell(c), linha.get(c), dataStyle, dataHoraStyle, numeroStyle);
                }
            }

            for (int c = 0; c < colunas.size(); c++) {
                sheet.autoSizeColumn(c);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao gerar XLSX", e);
        }
    }

    private static void preencherCelula(
        Cell cell,
        Object valor,
        CellStyle dataStyle,
        CellStyle dataHoraStyle,
        CellStyle numeroStyle
    ) {
        if (valor == null) {
            cell.setBlank();
            return;
        }
        if (valor instanceof LocalDate ld) {
            cell.setCellValue(Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            cell.setCellStyle(dataStyle);
            return;
        }
        if (valor instanceof LocalDateTime ldt) {
            cell.setCellValue(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));
            cell.setCellStyle(dataHoraStyle);
            return;
        }
        if (ValorCelulaUtil.isNumero(valor)) {
            cell.setCellValue(ValorCelulaUtil.comoDouble(valor));
            cell.setCellStyle(numeroStyle);
            return;
        }
        cell.setCellValue(ValorCelulaUtil.comoTexto(valor));
    }

    private static String sanitizarNomeAba(String titulo) {
        String nome = titulo == null || titulo.isBlank() ? "Relatorio" : titulo;
        nome = nome.replaceAll("[\\\\/?*\\[\\]:]", " ").trim();
        return nome.length() > 31 ? nome.substring(0, 31) : nome;
    }
}
