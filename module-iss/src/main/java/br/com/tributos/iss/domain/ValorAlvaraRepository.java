package br.com.tributos.iss.domain;

import java.util.List;
import java.util.UUID;

public interface ValorAlvaraRepository {

    ValorAlvara salvar(ValorAlvara valorAlvara);

    List<ValorAlvara> listarPorTipoAlvara(UUID tipoAlvaraId);
}
