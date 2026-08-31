package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.TipoAlvara;
import br.com.tributos.iss.domain.TipoAlvaraRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class TipoAlvaraRepositoryAdapter implements TipoAlvaraRepository {

    private final TipoAlvaraJpaRepository jpaRepository;

    public TipoAlvaraRepositoryAdapter(TipoAlvaraJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TipoAlvara> listar() {
        return jpaRepository.findAll().stream().map(TipoAlvaraRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<TipoAlvara> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(TipoAlvaraRepositoryAdapter::paraDominio);
    }

    @Override
    public TipoAlvara salvar(TipoAlvara tipoAlvara) {
        UUID tenantId = TenantContext.getObrigatorio();
        TipoAlvaraJpaEntity entidade = jpaRepository.findById(tipoAlvara.id())
            .orElseGet(() -> {
                TipoAlvaraJpaEntity nova = new TipoAlvaraJpaEntity();
                nova.setId(tipoAlvara.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setNome(tipoAlvara.nome());
        entidade.setValorBase(tipoAlvara.valorBase());
        entidade.setDiasValidade(tipoAlvara.diasValidade());
        entidade.setAtivo(tipoAlvara.ativo());
        entidade.setAnoVigencia(tipoAlvara.anoVigencia());
        entidade.setIdentificacaoModeloDocumento(tipoAlvara.identificacaoModeloDocumento());
        entidade.setPermiteValorDinamico(tipoAlvara.permiteValorDinamico());
        entidade.setPermiteCalculoValor(tipoAlvara.permiteCalculoValor());
        entidade.setUnidadeMedidaDescritivo(tipoAlvara.unidadeMedidaDescritivo());
        entidade.setHabilitarValidade(tipoAlvara.habilitarValidade());
        entidade.setHabilitarCalculoVencimento(tipoAlvara.habilitarCalculoVencimento());
        entidade.setBaseVencimento(tipoAlvara.baseVencimento());
        entidade.setDiasMesesVencimento(tipoAlvara.diasMesesVencimento());
        entidade.setTitulo(tipoAlvara.titulo());
        entidade.setSecretaria(tipoAlvara.secretaria());
        entidade.setCargo(tipoAlvara.cargo());
        entidade.setAssinaturaDocumentoId(tipoAlvara.assinaturaDocumentoId());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public boolean existePorNome(String nome, UUID excetoId) {
        if (excetoId == null) {
            return jpaRepository.existsByNome(nome);
        }
        return jpaRepository.existsByNomeAndIdNot(nome, excetoId);
    }

    static TipoAlvara paraDominio(TipoAlvaraJpaEntity entidade) {
        return new TipoAlvara(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getNome(),
            entidade.getValorBase(),
            entidade.getDiasValidade(),
            entidade.isAtivo(),
            entidade.getAnoVigencia(),
            entidade.getIdentificacaoModeloDocumento(),
            entidade.isPermiteValorDinamico(),
            entidade.isPermiteCalculoValor(),
            entidade.getUnidadeMedidaDescritivo(),
            entidade.isHabilitarValidade(),
            entidade.isHabilitarCalculoVencimento(),
            entidade.getBaseVencimento(),
            entidade.getDiasMesesVencimento(),
            entidade.getTitulo(),
            entidade.getSecretaria(),
            entidade.getCargo(),
            entidade.getAssinaturaDocumentoId()
        );
    }
}
