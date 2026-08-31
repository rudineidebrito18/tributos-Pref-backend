package br.com.tributos.identity.application;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.tributos.identity.domain.AmbientePixBb;
import br.com.tributos.identity.domain.ConfiguracaoPixBb;
import br.com.tributos.identity.domain.ConfiguracaoPixBbRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalvarConfiguracaoPixServiceTest {

    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-4000-8000-000000000001");

    @Mock
    private ConfiguracaoPixBbRepository repository;

    @InjectMocks
    private SalvarConfiguracaoPixService service;

    @Test
    void deveDesativarOutrasAoAtivarNovaConfiguracao() {
        TenantContext.set(TENANT_ID);
        try {
            ConfiguracaoPixBb sandbox = config(AmbientePixBb.SANDBOX);
            ConfiguracaoPixBb producao = config(AmbientePixBb.PRODUCAO);

            when(repository.buscarPorTenantEAmbiente(TENANT_ID, AmbientePixBb.SANDBOX))
                .thenReturn(Optional.of(sandbox));
            when(repository.buscarPorTenantEAmbiente(TENANT_ID, AmbientePixBb.PRODUCAO))
                .thenReturn(Optional.empty());
            when(repository.salvar(any(ConfiguracaoPixBb.class))).thenAnswer(inv -> {
                ConfiguracaoPixBb c = inv.getArgument(0);
                if (c.getId() == null) {
                    c.setId(UUID.randomUUID());
                }
                return c;
            });

            service.executar(comando(AmbientePixBb.SANDBOX, true));
            service.executar(comando(AmbientePixBb.PRODUCAO, true));

            verify(repository).desativarOutrasDoTenant(eq(TENANT_ID), eq(sandbox.getId()));
            verify(repository, times(2)).desativarOutrasDoTenant(eq(TENANT_ID), any(UUID.class));
        } finally {
            TenantContext.clear();
        }
    }

    private static ConfiguracaoPixBb config(AmbientePixBb ambiente) {
        ConfiguracaoPixBb c = new ConfiguracaoPixBb();
        c.setId(UUID.randomUUID());
        c.setTenantId(TENANT_ID);
        c.setAmbiente(ambiente);
        c.setClientSecret("segredo");
        return c;
    }

    private static SalvarConfiguracaoPixComando comando(AmbientePixBb ambiente, boolean ativo) {
        return new SalvarConfiguracaoPixComando(
            ambiente,
            ativo,
            "client",
            "segredo",
            "dev-key",
            "escopos",
            "123456",
            "chave",
            "N",
            null,
            null,
            null,
            null
        );
    }
}
