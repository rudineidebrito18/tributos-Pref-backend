package br.com.tributos.cadastro.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.application.ports.ArmazenamentoArquivo;
import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class ExcluirDocumentoService {

    private final DocumentoRepository documentoRepository;
    private final ArmazenamentoArquivo armazenamentoArquivo;

    public ExcluirDocumentoService(DocumentoRepository documentoRepository, ArmazenamentoArquivo armazenamentoArquivo) {
        this.documentoRepository = documentoRepository;
        this.armazenamentoArquivo = armazenamentoArquivo;
    }

    @Transactional
    public void executar(UUID pessoaId, UUID documentoId) {
        Documento documento = documentoRepository.buscarPorId(documentoId)
            .filter(d -> d.pessoaId().equals(pessoaId))
            .orElseThrow(() -> new NotFoundException("Documento não encontrado."));

        documentoRepository.excluir(documentoId);
        armazenamentoArquivo.excluir(documento.storageChave());
    }
}
