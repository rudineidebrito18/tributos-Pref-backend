package br.com.tributos.cadastro.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PessoaRepository {

    Pessoa salvar(Pessoa pessoa);

    Optional<Pessoa> buscarPorId(UUID id);

    Optional<Pessoa> buscarPorCpfCnpj(String cpfCnpj);

    boolean existePorCpfCnpj(String cpfCnpj, UUID ignorarPessoaId);

    Page<Pessoa> listar(String busca, Pageable pageable);
}
