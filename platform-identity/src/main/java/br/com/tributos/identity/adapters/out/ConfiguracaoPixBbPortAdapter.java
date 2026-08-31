package br.com.tributos.identity.adapters.out;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.domain.ConfiguracaoPixBb;
import br.com.tributos.identity.domain.ConfiguracaoPixBbRepository;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixBbPort;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixOperacional;

@Component
public class ConfiguracaoPixBbPortAdapter implements ConfiguracaoPixBbPort {

    private final ConfiguracaoPixBbRepository repository;

    public ConfiguracaoPixBbPortAdapter(ConfiguracaoPixBbRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ConfiguracaoPixOperacional> buscarAtiva(UUID tenantId) {
        return repository.buscarAtivaPorTenant(tenantId).map(ConfiguracaoPixBbPortAdapter::mapear);
    }

    private static ConfiguracaoPixOperacional mapear(ConfiguracaoPixBb config) {
        return new ConfiguracaoPixOperacional(
            config.getTenantId(),
            config.getAmbiente().name(),
            config.getClientId(),
            config.getClientSecret(),
            config.getDeveloperApplicationKey(),
            config.getEscopos(),
            config.getNumeroConvenio(),
            config.getChavePix(),
            config.getIndicadorCodigoBarras(),
            config.getCertificadoPath(),
            config.getCertificadoSenha(),
            config.getWebhookToken()
        );
    }
}
