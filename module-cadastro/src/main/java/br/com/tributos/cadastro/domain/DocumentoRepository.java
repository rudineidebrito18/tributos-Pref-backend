package br.com.tributos.cadastro.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentoRepository {

    Documento salvar(Documento documento);

    List<Documento> listarPorPessoa(UUID pessoaId);

    Optional<Documento> buscarPorId(UUID id);

    void excluir(UUID id);
}
