package br.com.tributos.itbi.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.itbi.domain.GuiaItbi;
import br.com.tributos.itbi.domain.GuiaItbiRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BuscarGuiaItbiService {

    private final GuiaItbiRepository guiaItbiRepository;

    public BuscarGuiaItbiService(GuiaItbiRepository guiaItbiRepository) {
        this.guiaItbiRepository = guiaItbiRepository;
    }

    public GuiaItbi executar(UUID id) {
        return guiaItbiRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Guia ITBI não encontrada."));
    }
}
