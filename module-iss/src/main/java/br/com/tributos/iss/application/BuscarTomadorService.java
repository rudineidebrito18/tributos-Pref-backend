package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Tomador;
import br.com.tributos.iss.domain.TomadorRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BuscarTomadorService {

    private final TomadorRepository tomadorRepository;

    public BuscarTomadorService(TomadorRepository tomadorRepository) {
        this.tomadorRepository = tomadorRepository;
    }

    @Transactional(readOnly = true)
    public Tomador executar(UUID id) {
        return tomadorRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Tomador não encontrado."));
    }
}
