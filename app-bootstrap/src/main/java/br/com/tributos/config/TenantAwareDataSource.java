package br.com.tributos.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;

import br.com.tributos.kernel.tenancy.TenantContext;

/**
 * Envolve o {@link DataSource} real para, a cada conexão emprestada do pool, executar
 * {@code SET LOCAL app.current_tenant}, que é o que as políticas RLS das migrations
 * (ver {@code platform-identity/.../V1__core_platform.sql}) consultam via
 * {@code current_setting('app.current_tenant', true)}.
 *
 * <p>{@code SET LOCAL} só vale dentro de uma transação — funciona aqui porque, com pool de
 * conexões, {@code getConnection()} é chamado de novo a cada transação nova (Spring/Hibernate
 * pedem uma conexão do pool no início de cada transação e a devolvem ao final); não existe
 * o caso de uma conexão de longa duração sendo reaproveitada por várias transações sem
 * passar por aqui de novo.
 *
 * <p>Requisições sem tenant resolvido (endpoints públicos) simplesmente não executam o
 * `SET LOCAL` — as tabelas que essas rotas consultam (ex.: {@code tenant}) não têm RLS.
 */
public class TenantAwareDataSource extends DelegatingDataSource {

    public TenantAwareDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = obtainTargetDataSource().getConnection();
        aplicarTenantAtual(connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = obtainTargetDataSource().getConnection(username, password);
        aplicarTenantAtual(connection);
        return connection;
    }

    private void aplicarTenantAtual(Connection connection) throws SQLException {
        UUID tenantId = TenantContext.get();
        if (tenantId == null) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            // UUID#toString() só produz hexadecimais e hifens — não há necessidade de
            // parametrizar via PreparedStatement para este valor específico.
            statement.execute("SET LOCAL app.current_tenant = '" + tenantId + "'");
        }
    }
}
