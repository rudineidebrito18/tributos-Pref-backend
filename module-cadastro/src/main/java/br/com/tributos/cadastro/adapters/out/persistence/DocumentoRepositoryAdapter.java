package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoCompartilhamento;
import br.com.tributos.cadastro.domain.DocumentoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class DocumentoRepositoryAdapter implements DocumentoRepository {

    private final DocumentoJpaRepository jpaRepository;
    private final DocumentoCompartilhamentoJpaRepository compartilhamentoJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public DocumentoRepositoryAdapter(
        DocumentoJpaRepository jpaRepository,
        DocumentoCompartilhamentoJpaRepository compartilhamentoJpaRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.compartilhamentoJpaRepository = compartilhamentoJpaRepository;
    }

    @Override
    public Documento salvar(Documento documento) {
        return paraDominio(jpaRepository.save(new DocumentoJpaEntity(
            documento.id(), documento.tenantId(), documento.pessoaId(), documento.tipo(), documento.titulo(),
            documento.categoriaId(), documento.nomeArquivo(), documento.conteudoTipo(), documento.tamanhoBytes(),
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

    @Override
    public Page<Documento> listarSistema(
        UUID tenantId,
        String titulo,
        UUID categoriaId,
        String nomeArquivo,
        Pageable pageable
    ) {
        return jpaRepository.listarSistema(tenantId, titulo, categoriaId, nomeArquivo, pageable)
            .map(DocumentoRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<Documento> listarCompartilhadosComUsuario(
        UUID tenantId,
        UUID usuarioId,
        String titulo,
        UUID categoriaId,
        String nomeArquivo,
        Pageable pageable
    ) {
        return jpaRepository.listarCompartilhadosComUsuario(
            tenantId, usuarioId, titulo, categoriaId, nomeArquivo, pageable
        ).map(DocumentoRepositoryAdapter::paraDominio);
    }

    @Override
    public boolean possuiCompartilhamento(UUID documentoId, UUID usuarioId) {
        return compartilhamentoJpaRepository.existsByDocumentoIdAndUsuarioId(documentoId, usuarioId);
    }

    @Override
    public void salvarCompartilhamento(DocumentoCompartilhamento compartilhamento) {
        compartilhamentoJpaRepository.save(new DocumentoCompartilhamentoJpaEntity(
            compartilhamento.id(), compartilhamento.tenantId(), compartilhamento.documentoId(),
            compartilhamento.usuarioId()
        ));
    }

    @Override
    public boolean existeUsuarioAtivoNoTenant(UUID tenantId, UUID usuarioId) {
        Boolean existe = (Boolean) entityManager.createNativeQuery("""
            SELECT EXISTS(
                SELECT 1 FROM usuario u
                WHERE u.id = :usuarioId AND u.tenant_id = :tenantId AND u.ativo = true
            )
            """)
            .setParameter("usuarioId", usuarioId)
            .setParameter("tenantId", tenantId)
            .getSingleResult();
        return Boolean.TRUE.equals(existe);
    }

    private static Documento paraDominio(DocumentoJpaEntity entidade) {
        return new Documento(
            entidade.getId(), entidade.getTenantId(), entidade.getPessoaId(), entidade.getTipo(),
            entidade.getTitulo(), entidade.getCategoriaId(), entidade.getNomeArquivo(), entidade.getConteudoTipo(),
            entidade.getTamanhoBytes(), entidade.getStorageChave(), entidade.isCompartilhado(), entidade.getCriadoEm()
        );
    }
}
