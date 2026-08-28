package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ContribuinteRepositoryAdapter implements ContribuinteRepository {

    private final ContribuinteJpaRepository jpaRepository;

    public ContribuinteRepositoryAdapter(ContribuinteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Contribuinte salvar(Contribuinte contribuinte) {
        UUID tenantId = TenantContext.getObrigatorio();
        ContribuinteJpaEntity entidade = jpaRepository.findById(contribuinte.id())
            .orElseGet(() -> {
                ContribuinteJpaEntity nova = new ContribuinteJpaEntity();
                nova.setId(contribuinte.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setPessoaId(contribuinte.pessoaId());
        entidade.setInscricaoMunicipal(contribuinte.inscricaoMunicipal());
        entidade.setTipoContribuinteId(contribuinte.tipoContribuinteId());
        entidade.setSituacaoCadastralId(contribuinte.situacaoCadastralId());
        entidade.setStatusCredenciamentoId(contribuinte.statusCredenciamentoId());
        entidade.setRegimeTributarioId(contribuinte.regimeTributarioId());
        entidade.setNomeContador(contribuinte.nomeContador());
        entidade.setEmailContador(contribuinte.emailContador());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Contribuinte> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(ContribuinteRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<Contribuinte> listar(String busca, Pageable pageable) {
        String termo = busca == null ? "" : busca.trim();
        return jpaRepository.buscarComFiltro(termo.isEmpty() ? null : termo, pageable)
            .map(ContribuinteRepositoryAdapter::paraDominio);
    }

    @Override
    public boolean existePorInscricaoMunicipal(String inscricaoMunicipal, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByInscricaoMunicipal(inscricaoMunicipal);
        }
        return jpaRepository.existsByInscricaoMunicipalAndIdNot(inscricaoMunicipal, ignorarId);
    }

    @Override
    public boolean existePorPessoaId(UUID pessoaId, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByPessoaId(pessoaId);
        }
        return jpaRepository.existsByPessoaIdAndIdNot(pessoaId, ignorarId);
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    private static Contribuinte paraDominio(ContribuinteJpaEntity entidade) {
        return new Contribuinte(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getPessoaId(),
            entidade.getInscricaoMunicipal(),
            entidade.getTipoContribuinteId(),
            entidade.getSituacaoCadastralId(),
            entidade.getStatusCredenciamentoId(),
            entidade.getRegimeTributarioId(),
            entidade.getNomeContador(),
            entidade.getEmailContador()
        );
    }
}
