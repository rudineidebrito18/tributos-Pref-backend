package br.com.tributos.itbi.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.itbi.domain.PapelParteTransmissao;
import br.com.tributos.itbi.domain.ParteTransmissao;
import br.com.tributos.itbi.domain.ParteTransmissaoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ParteTransmissaoRepositoryAdapter implements ParteTransmissaoRepository {

    private final ParteTransmissaoJpaRepository jpaRepository;

    public ParteTransmissaoRepositoryAdapter(ParteTransmissaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ParteTransmissao salvar(ParteTransmissao parte) {
        return paraDominio(jpaRepository.save(paraEntidade(parte)));
    }

    @Override
    public Optional<ParteTransmissao> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public List<ParteTransmissao> listarPorGuiaEPapel(UUID guiaId, PapelParteTransmissao papel) {
        return jpaRepository.findByGuiaIdAndPapelOrderByPrincipalDescPorcentagemDesc(guiaId, papel)
            .stream()
            .map(this::paraDominio)
            .toList();
    }

    @Override
    public boolean existePorGuiaContribuinteEPapel(
        UUID guiaId,
        UUID contribuinteId,
        PapelParteTransmissao papel,
        UUID idExcluir
    ) {
        UUID excluir = idExcluir != null ? idExcluir : UUID.randomUUID();
        return jpaRepository.existsByGuiaIdAndContribuinteIdAndPapelAndIdNot(guiaId, contribuinteId, papel, excluir);
    }

    @Override
    public void remover(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<ParteTransmissao> buscarPrincipalPorGuiaEPapel(UUID guiaId, PapelParteTransmissao papel) {
        return jpaRepository.findFirstByGuiaIdAndPapelAndPrincipalTrueOrderByPorcentagemDesc(guiaId, papel)
            .map(this::paraDominio);
    }

    private ParteTransmissao paraDominio(ParteTransmissaoJpaEntity entidade) {
        return new ParteTransmissao(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getGuiaId(),
            entidade.getContribuinteId(),
            entidade.getPapel(),
            entidade.getPorcentagem(),
            entidade.isPrincipal()
        );
    }

    private ParteTransmissaoJpaEntity paraEntidade(ParteTransmissao parte) {
        ParteTransmissaoJpaEntity entidade = jpaRepository.findById(parte.id())
            .orElseGet(() -> {
                ParteTransmissaoJpaEntity nova = new ParteTransmissaoJpaEntity();
                nova.setId(parte.id());
                nova.setTenantId(TenantContext.getObrigatorio());
                return nova;
            });
        entidade.setGuiaId(parte.guiaId());
        entidade.setContribuinteId(parte.contribuinteId());
        entidade.setPapel(parte.papel());
        entidade.setPorcentagem(parte.porcentagem());
        entidade.setPrincipal(parte.principal());
        return entidade;
    }
}
