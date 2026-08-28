package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ImovelRepository {

    Imovel salvar(Imovel imovel);

    Optional<Imovel> buscarPorId(UUID id);

    Page<Imovel> listar(String busca, Pageable pageable);

    long proximoNumeroCadastro();

    Optional<Imovel> buscarPorCodigoLegado(String codigoLegado);

    List<Imovel> listarAtivosComZonaEDestinacao();

    long contarAtivosSemZona();
}
