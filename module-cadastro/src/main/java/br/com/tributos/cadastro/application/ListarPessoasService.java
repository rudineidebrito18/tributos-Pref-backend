package br.com.tributos.cadastro.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.Pessoa;
import br.com.tributos.cadastro.domain.PessoaRepository;

@Service
public class ListarPessoasService {

    private final PessoaRepository pessoaRepository;

    public ListarPessoasService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional(readOnly = true)
    public Page<Pessoa> executar(String busca, Pageable pageable) {
        return pessoaRepository.listar(busca, pageable);
    }
}
