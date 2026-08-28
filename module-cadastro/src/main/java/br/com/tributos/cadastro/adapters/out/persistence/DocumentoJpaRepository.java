package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoJpaRepository extends JpaRepository<DocumentoJpaEntity, UUID> {

    List<DocumentoJpaEntity> findByPessoaIdOrderByCriadoEmDesc(UUID pessoaId);
}
