package br.com.tributos.cadastro.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentoCategoriaRepository {

    DocumentoCategoria salvar(DocumentoCategoria categoria);

    Optional<DocumentoCategoria> buscarPorId(UUID id);

    List<DocumentoCategoria> listar();

    boolean existePorNome(String nome, UUID excluirId);

    void excluir(UUID id);
}
