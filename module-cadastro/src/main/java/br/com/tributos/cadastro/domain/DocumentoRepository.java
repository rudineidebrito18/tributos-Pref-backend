package br.com.tributos.cadastro.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentoRepository {

    Documento salvar(Documento documento);

    List<Documento> listarPorPessoa(UUID pessoaId);

    Optional<Documento> buscarPorId(UUID id);

    void excluir(UUID id);

    Page<Documento> listarSistema(
        UUID tenantId,
        String titulo,
        UUID categoriaId,
        String nomeArquivo,
        Pageable pageable
    );

    Page<Documento> listarCompartilhadosComUsuario(
        UUID tenantId,
        UUID usuarioId,
        String titulo,
        UUID categoriaId,
        String nomeArquivo,
        Pageable pageable
    );

    boolean possuiCompartilhamento(UUID documentoId, UUID usuarioId);

    void salvarCompartilhamento(DocumentoCompartilhamento compartilhamento);

    boolean existeUsuarioAtivoNoTenant(UUID tenantId, UUID usuarioId);
}
