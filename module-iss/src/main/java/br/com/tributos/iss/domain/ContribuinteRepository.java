package br.com.tributos.iss.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContribuinteRepository {

    Contribuinte salvar(Contribuinte contribuinte);

    Optional<Contribuinte> buscarPorId(UUID id);

    Page<Contribuinte> listar(String busca, Pageable pageable);

    boolean existePorInscricaoMunicipal(String inscricaoMunicipal, UUID ignorarId);

    boolean existePorPessoaId(UUID pessoaId, UUID ignorarId);

    void excluir(UUID id);
}
