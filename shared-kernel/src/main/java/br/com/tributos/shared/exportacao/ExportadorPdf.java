package br.com.tributos.shared.exportacao;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class ExportadorPdf implements ExportadorRelatorio {

    private static final DateTimeFormatter DATA_EMISSAO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public FormatoExportacao formato() {
        return FormatoExportacao.PDF;
    }

    @Override
    public byte[] exportar(String titulo, String nomeTenant, List<String> colunas, List<List<Object>> linhas) {
        try (var out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new CabecalhoRodapePdf(titulo, nomeTenant));
            document.open();

            PdfPTable table = new PdfPTable(colunas.size());
            table.setWidthPercentage(100);
            table.setHeaderRows(1);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 7);

            for (String coluna : colunas) {
                PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (List<Object> linha : linhas) {
                for (Object valor : linha) {
                    table.addCell(new Phrase(ValorCelulaUtil.comoTexto(valor), bodyFont));
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar PDF", e);
        }
    }

    private static final class CabecalhoRodapePdf extends PdfPageEventHelper {

        private final String titulo;
        private final String nomeTenant;
        private final String dataEmissao;

        CabecalhoRodapePdf(String titulo, String nomeTenant) {
            this.titulo = titulo == null ? "Relatório" : titulo;
            this.nomeTenant = nomeTenant == null ? "" : nomeTenant;
            this.dataEmissao = DATA_EMISSAO.format(LocalDate.now());
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Font font = FontFactory.getFont(FontFactory.HELVETICA, 8);
            String cabecalho = nomeTenant.isBlank()
                ? titulo + " — emitido em " + dataEmissao
                : nomeTenant + " — " + titulo + " — emitido em " + dataEmissao;
            com.lowagie.text.pdf.ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_LEFT,
                new Phrase(cabecalho, font),
                document.left(),
                document.top() + 20,
                0
            );
            String rodape = "Página " + writer.getPageNumber();
            com.lowagie.text.pdf.ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_CENTER,
                new Phrase(rodape, font),
                (document.right() + document.left()) / 2,
                document.bottom() - 10,
                0
            );
        }
    }
}
