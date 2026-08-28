package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoRepository;

@Component
public class DocumentoRepositoryAdapter implements DocumentoRepository {

    private final DocumentoJpaRepository jpaRepository;

    public DocumentoRepositoryAdapter(DocumentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Documento salvar(Documento documento) {
        return paraDominio(jpaRepository.save(new DocumentoJpaEntity(
            documento.id(), documento.tenantId(), documento.pessoaId(), documento.tipo(),
            documento.nomeArquivo(), documento.conteudoTipo(), documento.tamanhoBytes(),
            documento.storageChave(), documento.compartilhado(), documento.criadoEm()
        )));
    }

    @Override
    public List<Documento> listarPorPessoa(UUID pessoaId) {
        return jpaRepository.findByPessoaIdOrderByCriadoEmDesc(pessoaId).stream()
            .map(DocumentoRepositoryAdapter::paraDominio)
            .toList();
    }

    @Override
    public Optional<Documento> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(DocumentoRepositoryAdapter::paraDominio);
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    private static Documento paraDominio(DocumentoJpaEntity entidade) {
        return new Documento(
            entidade.getId(), entidade.getTenantId(), entidade.getPessoaId(), entidade.getTipo(),
            entidade.getNomeArquivo(), entidade.getConteudoTipo(), entidade.getTamanhoBytes(),
            entidade.getStorageChave(), entidade.isCompartilhado(), entidade.getCriadoEm()
        );
    }
}
