package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.ContribuinteAtividadeServico;
import br.com.tributos.iss.domain.ContribuinteAtividadeServicoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ContribuinteAtividadeServicoRepositoryAdapter implements ContribuinteAtividadeServicoRepository {

    private final ContribuinteAtividadeServicoJpaRepository jpaRepository;

    public ContribuinteAtividadeServicoRepositoryAdapter(ContribuinteAtividadeServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ContribuinteAtividadeServico> listarPorContribuinte(UUID contribuinteId) {
        return jpaRepository.findByContribuinteIdOrderByAtividadeIdAscServicoIdAsc(contribuinteId).stream()
            .map(ContribuinteAtividadeServicoRepositoryAdapter::paraDominio)
            .toList();
    }

    @Override
    public Optional<ContribuinteAtividadeServico> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(ContribuinteAtividadeServicoRepositoryAdapter::paraDominio);
    }

    @Override
    public ContribuinteAtividadeServico salvar(ContribuinteAtividadeServico vinculo) {
        UUID tenantId = TenantContext.getObrigatorio();
        ContribuinteAtividadeServicoJpaEntity entidade = jpaRepository.findById(vinculo.id())
            .orElseGet(() -> {
                ContribuinteAtividadeServicoJpaEntity nova = new ContribuinteAtividadeServicoJpaEntity();
                nova.setId(vinculo.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setContribuinteId(vinculo.contribuinteId());
        entidade.setAtividadeId(vinculo.atividadeId());
        entidade.setServicoId(vinculo.servicoId());
        entidade.setTributavel(vinculo.tributavel());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existeVinculo(UUID contribuinteId, UUID atividadeId, UUID servicoId) {
        return jpaRepository.existsByContribuinteIdAndAtividadeIdAndServicoId(contribuinteId, atividadeId, servicoId);
    }

    private static ContribuinteAtividadeServico paraDominio(ContribuinteAtividadeServicoJpaEntity entidade) {
        return new ContribuinteAtividadeServico(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getContribuinteId(),
            entidade.getAtividadeId(),
            entidade.getServicoId(),
            entidade.isTributavel()
        );
    }
}
