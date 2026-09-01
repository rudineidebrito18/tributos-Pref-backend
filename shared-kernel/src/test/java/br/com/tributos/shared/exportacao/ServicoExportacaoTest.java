package br.com.tributos.shared.exportacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class ServicoExportacaoTest {

    private final ServicoExportacao servico = new ServicoExportacao();

    @Test
    void csvDeveTerBomSeparadorPontoEVirgulaENeutralizarFormula() {
        var resultado = servico.exportar(
            FormatoExportacao.CSV,
            "Teste",
            "Prefeitura",
            List.of("Nome"),
            List.of(List.of("=cmd|'/c calc'!A1"))
        );

        assertThat(resultado.contentType()).isEqualTo(FormatoExportacao.CSV.contentType());
        String csv = new String(resultado.conteudo(), StandardCharsets.UTF_8);
        assertThat(csv).startsWith("\uFEFF");
        assertThat(csv).contains("'=cmd");
    }

    @Test
    void csvDeveEscaparAspasEPontoEVirgula() {
        var resultado = servico.exportar(
            FormatoExportacao.CSV,
            "Teste",
            "Prefeitura",
            List.of("Descricao"),
            List.of(List.of("valor; com \"aspas\""))
        );

        String csv = new String(resultado.conteudo(), StandardCharsets.UTF_8);
        assertThat(csv).contains("\"valor; com \"\"aspas\"\"\"");
    }

    @Test
    void xlsxDeveGerarCelulaNumerica() throws Exception {
        var resultado = servico.exportar(
            FormatoExportacao.XLSX,
            "Teste",
            "Prefeitura",
            List.of("Valor"),
            List.of(List.of(new BigDecimal("1234.56")))
        );

        assertThat(resultado.contentType()).isEqualTo(FormatoExportacao.XLSX.contentType());
        try (var workbook = new XSSFWorkbook(new java.io.ByteArrayInputStream(resultado.conteudo()))) {
            var cell = workbook.getSheetAt(0).getRow(1).getCell(0);
            assertThat(cell.getCellType()).isEqualTo(CellType.NUMERIC);
            assertThat(cell.getNumericCellValue()).isEqualTo(1234.56);
        }
    }

    @Test
    void pdfDeveGerarBytesValidos() {
        var linhas = java.util.stream.IntStream.range(0, 120)
            .mapToObj(i -> List.<Object>of("Linha " + i, new BigDecimal(i)))
            .toList();

        var resultado = servico.exportar(
            FormatoExportacao.PDF,
            "Relatorio extenso",
            "Prefeitura Demo",
            List.of("Descricao", "Valor"),
            linhas
        );

        assertThat(resultado.contentType()).isEqualTo(FormatoExportacao.PDF.contentType());
        assertThat(resultado.conteudo().length).isGreaterThan(100);
    }

    @Test
    void xmlDeveEscaparCaracteresEspeciais() throws Exception {
        var resultado = servico.exportar(
            FormatoExportacao.XML,
            "Teste",
            "Prefeitura",
            List.of("Conteudo"),
            List.of(List.of("A & B < C > D"))
        );

        assertThat(resultado.contentType()).isEqualTo(FormatoExportacao.XML.contentType());
        String xml = new String(resultado.conteudo(), StandardCharsets.UTF_8);
        assertThat(xml).contains("A &amp; B &lt; C &gt; D");

        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(resultado.conteudo()));
    }

    @Test
    void deveRejeitarMaisDeCinquentaMilLinhas() {
        var linhas = java.util.stream.IntStream.range(0, 50_001)
            .mapToObj(i -> List.<Object>of(i))
            .toList();

        assertThatThrownBy(() -> servico.exportar(
            FormatoExportacao.CSV,
            "Teste",
            "Prefeitura",
            List.of("Id"),
            linhas
        )).isInstanceOf(ExportacaoLimiteExcedidoException.class);
    }
}
