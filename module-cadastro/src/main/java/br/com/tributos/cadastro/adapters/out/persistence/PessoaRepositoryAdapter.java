package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.domain.Endereco;
import br.com.tributos.cadastro.domain.Pessoa;
import br.com.tributos.cadastro.domain.PessoaRepository;
import br.com.tributos.kernel.tenancy.TenantContext;
import br.com.tributos.kernel.vo.CpfCnpj;

@Component
public class PessoaRepositoryAdapter implements PessoaRepository {

    private final PessoaJpaRepository jpaRepository;

    public PessoaRepositoryAdapter(PessoaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pessoa salvar(Pessoa pessoa) {
        UUID tenantId = TenantContext.getObrigatorio();
        PessoaJpaEntity entidade = jpaRepository.findById(pessoa.getId())
            .orElseGet(() -> {
                PessoaJpaEntity nova = new PessoaJpaEntity();
                nova.setId(pessoa.getId());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setTipoPessoa(pessoa.getTipoPessoa());
        entidade.setCpfCnpj(pessoa.getCpfCnpj().apenasDigitos());
        entidade.setNome(pessoa.getNome());
        entidade.setNomeFantasia(pessoa.getNomeFantasia());
        entidade.setRazaoSocial(pessoa.getRazaoSocial());
        entidade.setDataNascimentoFundacao(pessoa.getDataNascimentoFundacao());
        entidade.setEmail(pessoa.getEmail());
        entidade.setTelefone1(pessoa.getTelefone1());
        entidade.setTelefone2(pessoa.getTelefone2());
        entidade.setAtivo(pessoa.isAtivo());

        entidade.getEnderecos().clear();
        for (Endereco endereco : pessoa.getEnderecos()) {
            EnderecoJpaEntity enderecoEntidade = new EnderecoJpaEntity();
            enderecoEntidade.setId(endereco.id() != null ? endereco.id() : UUID.randomUUID());
            enderecoEntidade.setTenantId(tenantId);
            enderecoEntidade.setPessoa(entidade);
            enderecoEntidade.setLogradouroTexto(endereco.logradouroTexto());
            enderecoEntidade.setNumero(endereco.numero());
            enderecoEntidade.setComplemento(endereco.complemento());
            enderecoEntidade.setBairroTexto(endereco.bairroTexto());
            enderecoEntidade.setCidadeId(endereco.cidadeId());
            enderecoEntidade.setCep(endereco.cep() != null ? CpfCnpj.normalizar(endereco.cep()) : null);
            enderecoEntidade.setPrincipal(endereco.principal());
            entidade.getEnderecos().add(enderecoEntidade);
        }

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Pessoa> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(PessoaRepositoryAdapter::paraDominio);
    }

    @Override
    public boolean existePorCpfCnpj(String cpfCnpj, UUID ignorarPessoaId) {
        if (ignorarPessoaId == null) {
            return jpaRepository.existsByCpfCnpj(cpfCnpj);
        }
        return jpaRepository.existsByCpfCnpjAndIdNot(cpfCnpj, ignorarPessoaId);
    }

    @Override
    public Page<Pessoa> listar(String busca, Pageable pageable) {
        String termo = busca == null ? "" : busca.trim();
        if (!termo.isEmpty() && termo.chars().allMatch(Character::isDigit)) {
            termo = CpfCnpj.normalizar(termo);
        }
        return jpaRepository.buscarComFiltro(termo.isEmpty() ? null : termo, pageable)
            .map(PessoaRepositoryAdapter::paraDominio);
    }

    private static Pessoa paraDominio(PessoaJpaEntity entidade) {
        List<Endereco> enderecos = entidade.getEnderecos().stream()
            .map(e -> new Endereco(
                e.getId(), e.getTenantId(), entidade.getId(),
                e.getLogradouroTexto(), e.getNumero(), e.getComplemento(),
                e.getBairroTexto(), e.getCidadeId(), e.getCep(), e.isPrincipal()
            ))
            .toList();

        Pessoa pessoa = new Pessoa(
            entidade.getId(), entidade.getTenantId(), entidade.getTipoPessoa(),
            CpfCnpj.de(entidade.getCpfCnpj()), entidade.getNome(), entidade.getNomeFantasia(),
            entidade.getRazaoSocial(), entidade.getDataNascimentoFundacao(),
            entidade.getEmail(), entidade.getTelefone1(), entidade.getTelefone2(), entidade.isAtivo()
        );
        pessoa.substituirEnderecos(new ArrayList<>(enderecos));
        return pessoa;
    }
}
