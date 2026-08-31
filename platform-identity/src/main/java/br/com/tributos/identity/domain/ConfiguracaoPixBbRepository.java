package br.com.tributos.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConfiguracaoPixBbRepository {

    List<ConfiguracaoPixBb> listarPorTenant(UUID tenantId);

    Optional<ConfiguracaoPixBb> buscarPorTenantEAmbiente(UUID tenantId, AmbientePixBb ambiente);

    Optional<ConfiguracaoPixBb> buscarAtivaPorTenant(UUID tenantId);

    ConfiguracaoPixBb salvar(ConfiguracaoPixBb configuracao);

    void desativarOutrasDoTenant(UUID tenantId, UUID excetoId);
}
