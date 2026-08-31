package br.com.tributos.identity.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.identity.domain.ConfiguracaoPixBb;
import br.com.tributos.identity.domain.ConfiguracaoPixBbRepository;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class SalvarConfiguracaoPixService {

    private final ConfiguracaoPixBbRepository repository;

    public SalvarConfiguracaoPixService(ConfiguracaoPixBbRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ConfiguracaoPixBb executar(SalvarConfiguracaoPixComando comando) {
        var tenantId = TenantContext.getObrigatorio();
        var existente = repository.buscarPorTenantEAmbiente(tenantId, comando.ambiente());

        ConfiguracaoPixBb configuracao = existente.orElseGet(ConfiguracaoPixBb::new);
        if (configuracao.getId() == null) {
            configuracao.setTenantId(tenantId);
            configuracao.setAmbiente(comando.ambiente());
        }

        configuracao.setAtivo(comando.ativo());
        configuracao.setClientId(comando.clientId());
        configuracao.setClientSecret(resolverSegredo(comando.clientSecret(), existente.map(ConfiguracaoPixBb::getClientSecret).orElse(null), "Client secret"));
        configuracao.setDeveloperApplicationKey(comando.developerApplicationKey());
        configuracao.setEscopos(comando.escopos());
        configuracao.setNumeroConvenio(comando.numeroConvenio());
        configuracao.setChavePix(comando.chavePix());
        configuracao.setIndicadorCodigoBarras(comando.indicadorCodigoBarras());
        configuracao.setCertificadoPath(comando.certificadoPath());
        configuracao.setCertificadoSenha(resolverSegredoOpcional(comando.certificadoSenha(), existente.map(ConfiguracaoPixBb::getCertificadoSenha).orElse(null)));
        configuracao.setWebhookUrl(comando.webhookUrl());
        configuracao.setWebhookToken(resolverSegredoOpcional(comando.webhookToken(), existente.map(ConfiguracaoPixBb::getWebhookToken).orElse(null)));

        ConfiguracaoPixBb salva = repository.salvar(configuracao);

        if (salva.isAtivo()) {
            repository.desativarOutrasDoTenant(tenantId, salva.getId());
            salva.setAtivo(true);
        }

        return salva;
    }

    private static String resolverSegredo(String novo, String anterior, String rotulo) {
        if (novo != null && !novo.isBlank()) {
            return novo;
        }
        if (anterior != null && !anterior.isBlank()) {
            return anterior;
        }
        throw new ValidationException(rotulo + " é obrigatório na primeira configuração.");
    }

    private static String resolverSegredoOpcional(String novo, String anterior) {
        if (novo != null && !novo.isBlank()) {
            return novo;
        }
        return anterior;
    }
}
