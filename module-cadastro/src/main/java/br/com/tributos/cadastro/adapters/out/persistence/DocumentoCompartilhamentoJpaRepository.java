package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoCompartilhamentoJpaRepository extends JpaRepository<DocumentoCompartilhamentoJpaEntity, UUID> {

    boolean existsByDocumentoIdAndUsuarioId(UUID documentoId, UUID usuarioId);
}
