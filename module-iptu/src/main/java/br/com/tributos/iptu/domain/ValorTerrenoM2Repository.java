package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ValorTerrenoM2Repository {

    ValorTerrenoM2 salvar(ValorTerrenoM2 valor);

    Optional<ValorTerrenoM2> buscarPorZonaEExercicio(UUID zonaFiscalId, int exercicio);

    List<ValorTerrenoM2> listarPorExercicio(int exercicio);
}
