package br.com.tributos.itbi.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NaturezaTransmissaoRepository {

    List<NaturezaTransmissao> listarAtivas();

    Optional<NaturezaTransmissao> buscarPorId(UUID id);
}
