package br.com.tributos.itbi.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.itbi.domain.TipoGuiaItbi;
import br.com.tributos.itbi.domain.TipoGuiaItbiRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class TipoGuiaItbiRepositoryAdapter implements TipoGuiaItbiRepository {

    private final TipoGuiaItbiJpaRepository jpaRepository;

    public TipoGuiaItbiRepositoryAdapter(TipoGuiaItbiJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TipoGuiaItbi> listar() {
        return jpaRepository.findAll().stream().map(this::paraDominio).toList();
    }

    @Override
    public List<TipoGuiaItbi> listarAtivos() {
        return jpaRepository.findByAtivoTrueOrderByNome().stream().map(this::paraDominio).toList();
    }

    @Override
    public Optional<TipoGuiaItbi> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public TipoGuiaItbi salvar(TipoGuiaItbi tipoGuia) {
        UUID tenantId = TenantContext.getObrigatorio();
        TipoGuiaItbiJpaEntity entidade = jpaRepository.findById(tipoGuia.id())
            .orElseGet(TipoGuiaItbiJpaEntity::new);
        if (entidade.getId() == null) {
            entidade.setId(tipoGuia.id());
            entidade.setTenantId(tenantId);
        }
        entidade.setNome(tipoGuia.nome());
        entidade.setAliquota(tipoGuia.aliquota());
        entidade.setAtivo(tipoGuia.ativo());
        entidade.setTipoCalculoId(tipoGuia.tipoCalculoId());
        entidade.setPermiteDesconto(tipoGuia.permiteDesconto());
        entidade.setHabilitaCalculoValor(tipoGuia.habilitaCalculoValor());
        entidade.setValor(tipoGuia.valor() != null ? tipoGuia.valor() : BigDecimal.ZERO);
        entidade.setValorParcela(tipoGuia.valorParcela());
        entidade.setSecretaria(tipoGuia.secretaria());
        entidade.setCargo(tipoGuia.cargo());
        entidade.setAssinaturaDocumentoId(tipoGuia.assinaturaDocumentoId());
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

    private TipoGuiaItbi paraDominio(TipoGuiaItbiJpaEntity e) {
        return new TipoGuiaItbi(
            e.getId(), e.getTenantId(), e.getNome(), e.getAliquota(), e.isAtivo(),
            e.getTipoCalculoId(), e.isPermiteDesconto(), e.isHabilitaCalculoValor(),
            e.getValor(), e.getValorParcela(), e.getSecretaria(), e.getCargo(),
            e.getAssinaturaDocumentoId()
        );
    }
}
