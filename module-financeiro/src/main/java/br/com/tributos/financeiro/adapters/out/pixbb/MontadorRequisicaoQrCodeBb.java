package br.com.tributos.financeiro.adapters.out.pixbb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.application.ports.GatewayPix.ComandoGerarQrCode;
import br.com.tributos.kernel.exception.RegraNegocioException;
import tools.jackson.databind.ObjectMapper;

@Component
public class MontadorRequisicaoQrCodeBb {

    private static final int MAX_DESCRICAO = 140;
    private static final int MAX_NOME = 200;
    private static final DateTimeFormatter DATA_VENCIMENTO = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final ObjectMapper JSON = new ObjectMapper();

    public String montarJson(ComandoGerarQrCode comando) {
        validarAntesDeChamar(comando);

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("numeroConvenio", comando.numeroConvenio());
        corpo.put("indicadorCodigoBarras", comando.indicadorCodigoBarras());
        corpo.put("codigoGuiaRecebimento", comando.codigoGuiaRecebimento());
        corpo.put("codigoSolicitacaoBancoCentralBrasil", comando.chavePix());
        corpo.put("valorOriginalSolicitacao", formatarValor(comando.valor()));

        if (comando.dataVencimento() != null) {
            corpo.put("dataVencimentoSolicitacao", comando.dataVencimento().format(DATA_VENCIMENTO));
        }

        if (comando.descricao() != null && !comando.descricao().isBlank()) {
            corpo.put("descricaoSolicitacaoPagamento", truncar(comando.descricao(), MAX_DESCRICAO));
        }

        if (comando.nomeDevedor() != null && !comando.nomeDevedor().isBlank()) {
            corpo.put("nomeDevedor", truncar(comando.nomeDevedor(), MAX_NOME));
        }

        if (comando.cpfDevedor() != null && !comando.cpfDevedor().isBlank()) {
            corpo.put("cpfDevedor", documentoSemZerosEsquerda(comando.cpfDevedor()));
        } else if (comando.cnpjDevedor() != null && !comando.cnpjDevedor().isBlank()) {
            corpo.put("cnpjDevedor", documentoSemZerosEsquerda(comando.cnpjDevedor()));
        }

        try {
            return JSON.writeValueAsString(corpo);
        } catch (RuntimeException ex) {
            throw new RegraNegocioException("Falha ao montar requisição PIX para o BB.");
        }
    }

    public void validarAntesDeChamar(ComandoGerarQrCode comando) {
        if ("N".equalsIgnoreCase(comando.indicadorCodigoBarras())
            && comando.codigoGuiaRecebimento() != null
            && comando.codigoGuiaRecebimento().matches("\\d{44}")) {
            throw new RegraNegocioException(
                "Com indicadorCodigoBarras = N, codigoGuiaRecebimento não pode ter 44 dígitos numéricos."
            );
        }
    }

    static String formatarValor(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    static String truncar(String texto, int maximo) {
        if (texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, maximo);
    }

    static String documentoSemZerosEsquerda(String documento) {
        if (documento == null || documento.isBlank()) {
            return null;
        }
        String apenasDigitos = documento.replaceAll("\\D", "");
        String semZeros = apenasDigitos.replaceFirst("^0+", "");
        return semZeros.isEmpty() ? "0" : semZeros;
    }
}
