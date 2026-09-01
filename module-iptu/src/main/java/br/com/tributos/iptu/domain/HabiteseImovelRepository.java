package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HabiteseImovelRepository {

    HabiteseImovel salvar(HabiteseImovel habitese);

    Optional<HabiteseImovel> buscarPorId(UUID id);

    Page<HabiteseImovel> listarPorImovel(UUID imovelId, Pageable pageable);

    long proximoNumero();

    boolean existeCodigoVerificacao(String codigoVerificacao);
}
