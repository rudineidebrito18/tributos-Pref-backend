package br.com.tributos.cadastro.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.Pessoa;
import br.com.tributos.cadastro.domain.PessoaRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BuscarPessoaService {

    private final PessoaRepository pessoaRepository;

    public BuscarPessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional(readOnly = true)
    public Pessoa executar(UUID id) {
        return pessoaRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Pessoa não encontrada."));
    }
}
