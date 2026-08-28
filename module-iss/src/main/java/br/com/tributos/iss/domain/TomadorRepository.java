package br.com.tributos.iss.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TomadorRepository {

    Tomador salvar(Tomador tomador);

    Optional<Tomador> buscarPorId(UUID id);

    Page<Tomador> listar(Pageable pageable);

    boolean existePorPessoaId(UUID pessoaId, UUID ignorarId);
}
