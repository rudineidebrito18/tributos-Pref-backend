package br.com.tributos.itbi.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuiaItbiRepository {

    GuiaItbi salvar(GuiaItbi guia);

    Optional<GuiaItbi> buscarPorId(UUID id);

    Page<GuiaItbi> listar(UUID imovelId, Pageable pageable);

    long proximoNumero();
}
