package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface NotaFiscalJpaRepository extends JpaRepository<NotaFiscalJpaEntity, UUID>, JpaSpecificationExecutor<NotaFiscalJpaEntity> {

    @Query("SELECT COALESCE(MAX(n.numero), 0) FROM NotaFiscalJpaEntity n")
    long findMaxNumero();
}
