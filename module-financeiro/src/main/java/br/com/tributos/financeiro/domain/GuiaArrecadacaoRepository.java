package br.com.tributos.financeiro.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuiaArrecadacaoRepository {

    GuiaArrecadacao salvar(GuiaArrecadacao guia);

    Optional<GuiaArrecadacao> buscarPorId(UUID id);

    Optional<GuiaArrecadacao> buscarPorOrigem(OrigemGuia origemTipo, UUID origemId);

    Optional<GuiaArrecadacao> buscarPorNumero(long numero);

    Page<GuiaArrecadacao> listar(
        TipoTributo tipoTributo,
        SituacaoGuia situacao,
        UUID contribuinteId,
        StatusPix statusPix,
        UUID formaPagamentoId,
        Pageable pageable
    );

    long proximoNumero();

    boolean possuiPendencia(UUID tenantId, UUID pessoaId);
}
