package br.com.tributos.iss.application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import br.com.tributos.iss.domain.Alvara;
import br.com.tributos.iss.domain.AlvaraRepository;
import br.com.tributos.iss.domain.CertidaoIss;
import br.com.tributos.iss.domain.CertidaoIssRepository;
import br.com.tributos.iss.domain.TipoAlvara;
import br.com.tributos.iss.domain.TipoAlvaraRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class GeradorPdfIssService {

    private final AlvaraRepository alvaraRepository;
    private final CertidaoIssRepository certidaoIssRepository;
    private final TipoAlvaraRepository tipoAlvaraRepository;

    public GeradorPdfIssService(
        AlvaraRepository alvaraRepository,
        CertidaoIssRepository certidaoIssRepository,
        TipoAlvaraRepository tipoAlvaraRepository
    ) {
        this.alvaraRepository = alvaraRepository;
        this.certidaoIssRepository = certidaoIssRepository;
        this.tipoAlvaraRepository = tipoAlvaraRepository;
    }

    public byte[] gerarPdfAlvara(UUID alvaraId) {
        Alvara alvara = alvaraRepository.buscarPorId(alvaraId)
            .orElseThrow(() -> new NotFoundException("Alvará não encontrado."));

        TipoAlvara tipo = tipoAlvaraRepository.buscarPorId(alvara.tipoAlvaraId())
            .orElseThrow(() -> new NotFoundException("Tipo de alvará não encontrado."));

        return gerarPdf(
            "Alvará ISS",
            "Número: " + alvara.numero(),
            "Tipo: " + tipo.nome(),
            "Contribuinte: " + alvara.contribuinteId(),
            "Data de expedição: " + alvara.dataExpedicao(),
            "Validade: " + alvara.validade(),
            "Situação fiscal: " + alvara.situacaoFiscal(),
            "Valor: " + alvara.valor(),
            "Código de verificação: " + alvara.codigoVerificacao()
        );
    }

    public byte[] gerarPdfCertidao(UUID certidaoId) {
        CertidaoIss certidao = certidaoIssRepository.buscarPorId(certidaoId)
            .orElseThrow(() -> new NotFoundException("Certidão não encontrada."));

        return gerarPdf(
            "Certidão ISS",
            "Número: " + certidao.numero(),
            "Tipo: " + certidao.tipo(),
            "Contribuinte: " + certidao.contribuinteId(),
            "Validade: " + certidao.validade(),
            "Código de verificação: " + certidao.codigoVerificacao()
        );
    }

    private static byte[] gerarPdf(String titulo, String... linhas) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph(titulo));
            for (String linha : linhas) {
                document.add(new Paragraph(linha));
            }
            document.close();
            return output.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("Falha ao gerar PDF.", e);
        }
    }
}
