package br.com.tributos.iptu.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.ImovelHabiteseTipo;
import br.com.tributos.iptu.domain.ImovelHabiteseTipoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ImovelHabiteseTipoRepositoryAdapter implements ImovelHabiteseTipoRepository {

    private final ImovelHabiteseTipoJpaRepository jpaRepository;

    public ImovelHabiteseTipoRepositoryAdapter(ImovelHabiteseTipoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ImovelHabiteseTipo> listar() {
        return jpaRepository.findAll().stream().map(ImovelHabiteseTipoRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<ImovelHabiteseTipo> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(ImovelHabiteseTipoRepositoryAdapter::paraDominio);
    }

    @Override
    public ImovelHabiteseTipo salvar(ImovelHabiteseTipo tipo) {
        UUID tenantId = TenantContext.getObrigatorio();
        ImovelHabiteseTipoJpaEntity entidade = jpaRepository.findById(tipo.id())
            .orElseGet(ImovelHabiteseTipoJpaEntity::new);
        if (entidade.getId() == null) {
            entidade.setId(tipo.id());
            entidade.setTenantId(tenantId);
        }
        entidade.setNome(tipo.nome());
        entidade.setAtivo(tipo.ativo());
        entidade.setTitulo(tipo.titulo());
        entidade.setPermiteDesconto(tipo.permiteDesconto());
        entidade.setHabilitaCalculoValor(tipo.habilitaCalculoValor());
        entidade.setValor(tipo.valor() != null ? tipo.valor() : BigDecimal.ZERO);
        entidade.setSecretaria(tipo.secretaria());
        entidade.setCargo(tipo.cargo());
        entidade.setAssinaturaDocumentoId(tipo.assinaturaDocumentoId());
        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorNome(String nome, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByNome(nome);
        }
        return jpaRepository.existsByNomeAndIdNot(nome, ignorarId);
    }

    private static ImovelHabiteseTipo paraDominio(ImovelHabiteseTipoJpaEntity entidade) {
        return new ImovelHabiteseTipo(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getNome(),
            entidade.isAtivo(),
            entidade.getTitulo(),
            entidade.isPermiteDesconto(),
            entidade.isHabilitaCalculoValor(),
            entidade.getValor(),
            entidade.getSecretaria(),
            entidade.getCargo(),
            entidade.getAssinaturaDocumentoId(),
            entidade.getCriadoEm()
        );
    }
}
