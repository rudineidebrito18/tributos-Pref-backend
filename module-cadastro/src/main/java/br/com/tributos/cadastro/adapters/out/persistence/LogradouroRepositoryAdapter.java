package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.domain.Logradouro;
import br.com.tributos.cadastro.domain.LogradouroRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class LogradouroRepositoryAdapter implements LogradouroRepository {

    private final LogradouroJpaRepository jpaRepository;

    public LogradouroRepositoryAdapter(LogradouroJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Logradouro> listar(UUID cidadeId, UUID bairroId) {
        return jpaRepository.listar(cidadeId, bairroId).stream().map(LogradouroRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<Logradouro> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(LogradouroRepositoryAdapter::paraDominio);
    }

    @Override
    public Logradouro salvar(Logradouro logradouro) {
        UUID tenantId = TenantContext.getObrigatorio();
        LogradouroJpaEntity entidade = jpaRepository.findById(logradouro.id()).orElseGet(LogradouroJpaEntity::new);
        if (entidade.getId() == null) {
            entidade.setId(logradouro.id());
            entidade.setTenantId(tenantId);
        }
        entidade.setCidadeId(logradouro.cidadeId());
        entidade.setBairroId(logradouro.bairroId());
        entidade.setTipo(logradouro.tipo());
        entidade.setNome(logradouro.nome());
        entidade.setCep(logradouro.cep());
        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    private static Logradouro paraDominio(LogradouroJpaEntity entidade) {
        return new Logradouro(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getCidadeId(),
            entidade.getBairroId(),
            entidade.getTipo(),
            entidade.getNome(),
            entidade.getCep(),
            entidade.getCriadoEm()
        );
    }
}
