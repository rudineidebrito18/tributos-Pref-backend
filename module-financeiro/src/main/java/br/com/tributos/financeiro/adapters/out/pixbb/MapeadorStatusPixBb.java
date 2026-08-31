package br.com.tributos.financeiro.adapters.out.pixbb;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.tributos.financeiro.domain.StatusPix;

/** Converte `estadoSolicitacao` retornado pelo BB para {@link StatusPix}. */
public final class MapeadorStatusPixBb {

    private static final Logger log = LoggerFactory.getLogger(MapeadorStatusPixBb.class);

    private MapeadorStatusPixBb() {
    }

    public static Optional<StatusPix> mapear(String estadoSolicitacao) {
        if (estadoSolicitacao == null || estadoSolicitacao.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(StatusPix.valueOf(estadoSolicitacao.trim()));
        } catch (IllegalArgumentException ex) {
            log.warn("Status PIX desconhecido retornado pelo BB: {}", estadoSolicitacao);
            return Optional.empty();
        }
    }
}
