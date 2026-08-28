package br.com.tributos.cadastro.application;

import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.application.ports.ArmazenamentoArquivo;
import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BaixarDocumentoService {

    private final DocumentoRepository documentoRepository;
    private final ArmazenamentoArquivo armazenamentoArquivo;

    public BaixarDocumentoService(DocumentoRepository documentoRepository, ArmazenamentoArquivo armazenamentoArquivo) {
        this.documentoRepository = documentoRepository;
        this.armazenamentoArquivo = armazenamentoArquivo;
    }

    @Transactional(readOnly = true)
    public ArquivoParaDownload executar(UUID pessoaId, UUID documentoId) {
        Documento documento = documentoRepository.buscarPorId(documentoId)
            .filter(d -> d.pessoaId().equals(pessoaId))
            .orElseThrow(() -> new NotFoundException("Documento não encontrado."));

        return new ArquivoParaDownload(
            documento.nomeArquivo(),
            MediaType.parseMediaType(documento.conteudoTipo()),
            new InputStreamResource(armazenamentoArquivo.ler(documento.storageChave()))
        );
    }

    public record ArquivoParaDownload(String nomeArquivo, MediaType conteudoTipo, InputStreamResource recurso) {
    }
}
