package br.com.tributos.identity.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.domain.AmbientePixBb;
import br.com.tributos.identity.domain.ConfiguracaoPixBb;
import br.com.tributos.identity.domain.ConfiguracaoPixBbRepository;
import br.com.tributos.kernel.tenancy.TenantContext;
import br.com.tributos.shared.cripto.CifradorSegredo;

@Component
public class ConfiguracaoPixBbRepositoryAdapter implements ConfiguracaoPixBbRepository {

    private final ConfiguracaoPixBbJpaRepository jpaRepository;
    private final CifradorSegredo cifradorSegredo;

    public ConfiguracaoPixBbRepositoryAdapter(
        ConfiguracaoPixBbJpaRepository jpaRepository,
        CifradorSegredo cifradorSegredo
    ) {
        this.jpaRepository = jpaRepository;
        this.cifradorSegredo = cifradorSegredo;
    }

    @Override
    public List<ConfiguracaoPixBb> listarPorTenant(UUID tenantId) {
        return jpaRepository.findByTenantIdOrderByAmbiente(tenantId).stream().map(this::paraDominio).toList();
    }

    @Override
    public Optional<ConfiguracaoPixBb> buscarPorTenantEAmbiente(UUID tenantId, AmbientePixBb ambiente) {
        return jpaRepository.findByTenantIdAndAmbiente(tenantId, ambiente).map(this::paraDominio);
    }

    @Override
    public Optional<ConfiguracaoPixBb> buscarAtivaPorTenant(UUID tenantId) {
        return jpaRepository.findByTenantIdAndAtivoTrue(tenantId).map(this::paraDominio);
    }

    @Override
    public ConfiguracaoPixBb salvar(ConfiguracaoPixBb configuracao) {
        UUID tenantId = configuracao.getTenantId() != null
            ? configuracao.getTenantId()
            : TenantContext.getObrigatorio();

        ConfiguracaoPixBbJpaEntity entidade = configuracao.getId() == null
            ? new ConfiguracaoPixBbJpaEntity()
            : jpaRepository.findById(configuracao.getId()).orElseGet(ConfiguracaoPixBbJpaEntity::new);

        if (entidade.getId() == null) {
            entidade.setId(configuracao.getId() != null ? configuracao.getId() : UUID.randomUUID());
        }
        entidade.setTenantId(tenantId);
        entidade.setAmbiente(configuracao.getAmbiente());
        entidade.setAtivo(configuracao.isAtivo());
        entidade.setClientId(configuracao.getClientId());
        entidade.setClientSecretCifrado(cifradorSegredo.cifrar(configuracao.getClientSecret()));
        entidade.setDeveloperApplicationKey(configuracao.getDeveloperApplicationKey());
        entidade.setEscopos(configuracao.getEscopos());
        entidade.setNumeroConvenio(configuracao.getNumeroConvenio());
        entidade.setChavePix(configuracao.getChavePix());
        entidade.setIndicadorCodigoBarras(configuracao.getIndicadorCodigoBarras());
        entidade.setCertificadoPath(configuracao.getCertificadoPath());
        entidade.setCertificadoSenhaCifrada(cifrarOpcional(configuracao.getCertificadoSenha()));
        entidade.setWebhookUrl(configuracao.getWebhookUrl());
        entidade.setWebhookTokenCifrado(cifrarOpcional(configuracao.getWebhookToken()));

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void desativarOutrasDoTenant(UUID tenantId, UUID excetoId) {
        jpaRepository.desativarOutras(tenantId, excetoId);
    }

    private String cifrarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return cifradorSegredo.cifrar(valor);
    }

    private String decifrarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return cifradorSegredo.decifrar(valor);
    }

    private ConfiguracaoPixBb paraDominio(ConfiguracaoPixBbJpaEntity entidade) {
        ConfiguracaoPixBb dominio = new ConfiguracaoPixBb();
        dominio.setId(entidade.getId());
        dominio.setTenantId(entidade.getTenantId());
        dominio.setAmbiente(entidade.getAmbiente());
        dominio.setAtivo(entidade.isAtivo());
        dominio.setClientId(entidade.getClientId());
        dominio.setClientSecret(decifrarOpcional(entidade.getClientSecretCifrado()));
        dominio.setDeveloperApplicationKey(entidade.getDeveloperApplicationKey());
        dominio.setEscopos(entidade.getEscopos());
        dominio.setNumeroConvenio(entidade.getNumeroConvenio());
        dominio.setChavePix(entidade.getChavePix());
        dominio.setIndicadorCodigoBarras(entidade.getIndicadorCodigoBarras());
        dominio.setCertificadoPath(entidade.getCertificadoPath());
        dominio.setCertificadoSenha(decifrarOpcional(entidade.getCertificadoSenhaCifrada()));
        dominio.setWebhookUrl(entidade.getWebhookUrl());
        dominio.setWebhookToken(decifrarOpcional(entidade.getWebhookTokenCifrado()));
        dominio.setCriadoEm(entidade.getCriadoEm());
        dominio.setAtualizadoEm(entidade.getAtualizadoEm());
        return dominio;
    }
}
