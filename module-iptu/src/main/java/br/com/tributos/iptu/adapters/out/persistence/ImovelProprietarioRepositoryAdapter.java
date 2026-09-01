package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.ImovelProprietario;
import br.com.tributos.iptu.domain.ImovelProprietarioRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ImovelProprietarioRepositoryAdapter implements ImovelProprietarioRepository {

    private final ImovelProprietarioJpaRepository jpaRepository;

    public ImovelProprietarioRepositoryAdapter(ImovelProprietarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ImovelProprietario salvar(ImovelProprietario proprietario) {
        UUID tenantId = TenantContext.getObrigatorio();
        ImovelProprietarioJpaEntity entidade = jpaRepository.findById(proprietario.id())
            .orElseGet(() -> {
                ImovelProprietarioJpaEntity nova = new ImovelProprietarioJpaEntity();
                nova.setId(proprietario.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setImovelId(proprietario.imovelId());
        entidade.setContribuinteId(proprietario.contribuinteId());
        entidade.setPorcentagem(proprietario.porcentagem());
        entidade.setProprietarioPrincipal(proprietario.proprietarioPrincipal());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public List<ImovelProprietario> listarPorImovel(UUID imovelId) {
        return jpaRepository.findByImovelIdOrderByProprietarioPrincipalDescCriadoEmAsc(imovelId)
            .stream()
            .map(ImovelProprietarioRepositoryAdapter::paraDominio)
            .toList();
    }

    @Override
    public Optional<ImovelProprietario> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(ImovelProprietarioRepositoryAdapter::paraDominio);
    }

    @Override
    public void remover(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void removerPorImovel(UUID imovelId) {
        jpaRepository.deleteByImovelId(imovelId);
    }

    @Override
    public boolean existePorImovelEContribuinte(UUID imovelId, UUID contribuinteId, UUID idExcluir) {
        UUID excluir = idExcluir != null ? idExcluir : UUID.randomUUID();
        return jpaRepository.existsByImovelIdAndContribuinteIdAndIdNot(imovelId, contribuinteId, excluir);
    }

    @Override
    public Optional<ImovelProprietario> buscarPrincipalPorImovel(UUID imovelId) {
        return jpaRepository.findFirstByImovelIdAndProprietarioPrincipalTrueOrderByCriadoEmAsc(imovelId)
            .map(ImovelProprietarioRepositoryAdapter::paraDominio);
    }

    private static ImovelProprietario paraDominio(ImovelProprietarioJpaEntity entidade) {
        return new ImovelProprietario(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getImovelId(),
            entidade.getContribuinteId(),
            entidade.getPorcentagem(),
            entidade.isProprietarioPrincipal()
        );
    }
}
