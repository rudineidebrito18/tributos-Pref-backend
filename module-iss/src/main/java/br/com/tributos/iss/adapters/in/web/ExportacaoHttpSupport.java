package br.com.tributos.iss.adapters.in.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import br.com.tributos.shared.exportacao.ResultadoExportacao;

public final class ExportacaoHttpSupport {

    private ExportacaoHttpSupport() {
    }

    public static ResponseEntity<byte[]> comoAnexo(ResultadoExportacao resultado) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resultado.nomeArquivo() + "\"")
            .contentType(MediaType.parseMediaType(resultado.contentType()))
            .body(resultado.conteudo());
    }
}
