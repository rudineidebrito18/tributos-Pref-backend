package br.com.tributos.cadastro.adapters.out;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.domain.Pessoa;
import br.com.tributos.cadastro.domain.PessoaRepository;
import br.com.tributos.kernel.cadastro.DadosDevedorPix;
import br.com.tributos.kernel.cadastro.DevedorPixPort;
import br.com.tributos.kernel.vo.CpfCnpj;

@Component
public class DevedorPixPortAdapter implements DevedorPixPort {

    private final PessoaRepository pessoaRepository;

    public DevedorPixPortAdapter(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Override
    public Optional<DadosDevedorPix> buscarPorPessoaId(UUID pessoaId) {
        return pessoaRepository.buscarPorId(pessoaId).map(DevedorPixPortAdapter::mapear);
    }

    private static DadosDevedorPix mapear(Pessoa pessoa) {
        String nome = pessoa.getRazaoSocial() != null && !pessoa.getRazaoSocial().isBlank()
            ? pessoa.getRazaoSocial()
            : pessoa.getNome();
        String cpf = null;
        String cnpj = null;
        if (pessoa.getCpfCnpj() != null) {
            if (pessoa.getCpfCnpj().tipo() == CpfCnpj.Tipo.CPF) {
                cpf = pessoa.getCpfCnpj().apenasDigitos();
            } else {
                cnpj = pessoa.getCpfCnpj().apenasDigitos();
            }
        }
        return new DadosDevedorPix(nome, cpf, cnpj);
    }
}
