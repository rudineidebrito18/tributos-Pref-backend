package br.com.tributos.kernel.audit;

/**
 * Porta para registro de auditoria — implementada em platform-identity (tabela
 * {@code log_auditoria}). Módulos de negócio dependem só desta interface.
 */
public interface AuditoriaPort {

    void registrar(RegistroAuditoria registro);
}
