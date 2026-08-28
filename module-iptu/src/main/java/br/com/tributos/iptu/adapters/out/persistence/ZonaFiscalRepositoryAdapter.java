package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.ZonaFiscal;
import br.com.tributos.iptu.domain.ZonaFiscalRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ZonaFiscalRepositoryAdapter implements ZonaFiscalRepository {

    private final ZonaFiscalJpaRepository jpaRepository;

    public ZonaFiscalRepositoryAdapter(ZonaFiscalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ZonaFiscal salvar(ZonaFiscal zonaFiscal) {
        UUID tenantId = TenantContext.getObrigatorio();
        ZonaFiscalJpaEntity entidade = jpaRepository.findById(zonaFiscal.id())
            .orElseGet(() -> {
                ZonaFiscalJpaEntity nova = new ZonaFiscalJpaEntity();
                nova.setId(zonaFiscal.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setNome(zonaFiscal.nome());
        entidade.setFatorValorizacao(zonaFiscal.fatorValorizacao());
        entidade.setAtivo(zonaFiscal.ativo());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<ZonaFiscal> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(ZonaFiscalRepositoryAdapter::paraDominio);
    }

    @Override
    public List<ZonaFiscal> listar() {
        return jpaRepository.findAllByOrderByNome().stream().map(ZonaFiscalRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public List<ZonaFiscal> listarAtivas() {
        return jpaRepository.findByAtivoTrueOrderByNome().stream().map(ZonaFiscalRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public boolean existe(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existePorNome(String nome, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByNome(nome);
        }
        return jpaRepository.existsByNomeAndIdNot(nome, ignorarId);
    }

    private static ZonaFiscal paraDominio(ZonaFiscalJpaEntity entidade) {
        return new ZonaFiscal(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getNome(),
            entidade.getFatorValorizacao(),
            entidade.isAtivo()
        );
    }
}
