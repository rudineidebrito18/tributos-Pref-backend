package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.Servico;
import br.com.tributos.iss.domain.ServicoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ServicoRepositoryAdapter implements ServicoRepository {

    private final ServicoJpaRepository jpaRepository;

    public ServicoRepositoryAdapter(ServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Servico> listar() {
        return jpaRepository.findAll().stream().map(ServicoRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<Servico> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(ServicoRepositoryAdapter::paraDominio);
    }

    @Override
    public Servico salvar(Servico servico) {
        UUID tenantId = TenantContext.getObrigatorio();
        ServicoJpaEntity entidade = jpaRepository.findById(servico.id())
            .orElseGet(() -> {
                ServicoJpaEntity nova = new ServicoJpaEntity();
                nova.setId(servico.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setCodigoLc116(servico.codigoLc116());
        entidade.setDescricao(servico.descricao());
        entidade.setAliquotaMinima(servico.aliquotaMinima());
        entidade.setAliquotaMaxima(servico.aliquotaMaxima());
        entidade.setAtivo(servico.ativo());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorCodigoLc116(String codigoLc116, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByCodigoLc116(codigoLc116);
        }
        return jpaRepository.existsByCodigoLc116AndIdNot(codigoLc116, ignorarId);
    }

    private static Servico paraDominio(ServicoJpaEntity entidade) {
        return new Servico(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getCodigoLc116(),
            entidade.getDescricao(),
            entidade.getAliquotaMinima(),
            entidade.getAliquotaMaxima(),
            entidade.isAtivo()
        );
    }
}
