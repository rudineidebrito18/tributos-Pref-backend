package br.com.tributos.iss.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlvaraRepository {

    Alvara salvar(Alvara alvara);

    Optional<Alvara> buscarPorId(UUID id);

    Optional<Alvara> buscarPorCodigoVerificacao(String codigoVerificacao);

    Page<Alvara> listar(UUID contribuinteId, Pageable pageable);

    long proximoNumero();
}
