package br.com.tributos.iptu.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CertidaoNegativaImovelRepository {

    CertidaoNegativaImovel salvar(CertidaoNegativaImovel certidao);

    Optional<CertidaoNegativaImovel> buscarPorId(UUID id);

    Page<CertidaoNegativaImovel> listarPorImovel(UUID imovelId, Pageable pageable);

    long proximoNumero();
}
