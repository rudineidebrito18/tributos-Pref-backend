package br.com.tributos.cadastro.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoRepository;
import br.com.tributos.cadastro.domain.PessoaRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class ListarDocumentosService {

    private final PessoaRepository pessoaRepository;
    private final DocumentoRepository documentoRepository;

    public ListarDocumentosService(PessoaRepository pessoaRepository, DocumentoRepository documentoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.documentoRepository = documentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Documento> executar(UUID pessoaId) {
        pessoaRepository.buscarPorId(pessoaId)
            .orElseThrow(() -> new NotFoundException("Pessoa não encontrada."));
        return documentoRepository.listarPorPessoa(pessoaId);
    }
}
