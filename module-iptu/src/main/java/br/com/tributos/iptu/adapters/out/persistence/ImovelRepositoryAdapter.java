package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.SituacaoImovel;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ImovelRepositoryAdapter implements ImovelRepository {

    private final ImovelJpaRepository jpaRepository;

    public ImovelRepositoryAdapter(ImovelJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Imovel salvar(Imovel imovel) {
        UUID tenantId = TenantContext.getObrigatorio();
        ImovelJpaEntity entidade = jpaRepository.findById(imovel.id())
            .orElseGet(() -> {
                ImovelJpaEntity nova = new ImovelJpaEntity();
                nova.setId(imovel.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setNumeroCadastro(imovel.numeroCadastro());
        entidade.setCodigoLegado(imovel.codigoLegado());
        entidade.setProprietarioId(imovel.proprietarioId());
        entidade.setTipoId(imovel.tipoId());
        entidade.setEnderecoId(imovel.enderecoId());
        entidade.setAreaTerreno(imovel.areaTerreno());
        entidade.setAreaConstruida(imovel.areaConstruida());
        entidade.setDestinacaoId(imovel.destinacaoId());
        entidade.setTipoEdificacaoId(imovel.tipoEdificacaoId());
        entidade.setTipoLimitacaoId(imovel.tipoLimitacaoId());
        entidade.setZonaFiscalId(imovel.zonaFiscalId());
        entidade.setValorVenalTerreno(imovel.valorVenalTerreno());
        entidade.setValorVenalConstrucao(imovel.valorVenalConstrucao());
        entidade.setSituacao(imovel.situacao());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Imovel> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(ImovelRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<Imovel> listar(String busca, Pageable pageable) {
        String termo = busca == null ? "" : busca.trim();
        return jpaRepository.buscarComFiltro(termo.isEmpty() ? null : termo, pageable)
            .map(ImovelRepositoryAdapter::paraDominio);
    }

    @Override
    public long proximoNumeroCadastro() {
        return jpaRepository.findMaxNumeroCadastro() + 1;
    }

    @Override
    public Optional<Imovel> buscarPorCodigoLegado(String codigoLegado) {
        if (codigoLegado == null || codigoLegado.isBlank()) {
            return Optional.empty();
        }
        return jpaRepository.findByCodigoLegado(codigoLegado.trim()).map(ImovelRepositoryAdapter::paraDominio);
    }

    @Override
    public List<Imovel> listarAtivosComZonaEDestinacao() {
        return jpaRepository.findBySituacaoAndZonaFiscalIdIsNotNullAndDestinacaoIdIsNotNull(SituacaoImovel.ATIVO)
            .stream()
            .map(ImovelRepositoryAdapter::paraDominio)
            .toList();
    }

    @Override
    public long contarAtivosSemZona() {
        return jpaRepository.countBySituacaoAndZonaFiscalIdIsNull(SituacaoImovel.ATIVO);
    }

    private static Imovel paraDominio(ImovelJpaEntity entidade) {
        return new Imovel(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getNumeroCadastro(),
            entidade.getCodigoLegado(),
            entidade.getProprietarioId(),
            entidade.getTipoId(),
            entidade.getEnderecoId(),
            entidade.getAreaTerreno(),
            entidade.getAreaConstruida(),
            entidade.getDestinacaoId(),
            entidade.getTipoEdificacaoId(),
            entidade.getTipoLimitacaoId(),
            entidade.getZonaFiscalId(),
            entidade.getValorVenalTerreno(),
            entidade.getValorVenalConstrucao(),
            entidade.getSituacao()
        );
    }
}
