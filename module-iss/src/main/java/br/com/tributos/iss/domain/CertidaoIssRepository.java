package br.com.tributos.iss.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CertidaoIssRepository {

    CertidaoIss salvar(CertidaoIss certidao);

    Optional<CertidaoIss> buscarPorId(UUID id);

    Optional<CertidaoIss> buscarPorCodigoVerificacao(String codigoVerificacao);

    Page<CertidaoIss> listar(UUID contribuinteId, Pageable pageable);

    long proximoNumero();
}
