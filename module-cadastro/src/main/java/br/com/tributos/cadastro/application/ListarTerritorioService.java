package br.com.tributos.cadastro.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.Cidade;
import br.com.tributos.cadastro.domain.Estado;
import br.com.tributos.cadastro.domain.TerritorioRepository;

@Service
public class ListarTerritorioService {

    private final TerritorioRepository territorioRepository;

    public ListarTerritorioService(TerritorioRepository territorioRepository) {
        this.territorioRepository = territorioRepository;
    }

    @Transactional(readOnly = true)
    public List<Estado> listarEstados() {
        return territorioRepository.listarEstados();
    }

    @Transactional(readOnly = true)
    public List<Cidade> listarCidades(String uf) {
        return territorioRepository.listarCidadesPorUf(uf);
    }
}
