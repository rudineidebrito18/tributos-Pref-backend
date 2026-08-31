package br.com.tributos.identity.application;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.domain.ConfiguracaoPixBb;
import br.com.tributos.identity.domain.ConfiguracaoPixBbRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class BuscarConfiguracaoPixService {

    private final ConfiguracaoPixBbRepository repository;

    public BuscarConfiguracaoPixService(ConfiguracaoPixBbRepository repository) {
        this.repository = repository;
    }

    public List<ConfiguracaoPixBb> listarDoTenantAtual() {
        return repository.listarPorTenant(TenantContext.getObrigatorio());
    }
}
