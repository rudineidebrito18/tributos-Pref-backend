package br.com.tributos.iss.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Tomador;
import br.com.tributos.iss.domain.TomadorRepository;

@Service
public class ListarTomadoresService {

    private final TomadorRepository tomadorRepository;

    public ListarTomadoresService(TomadorRepository tomadorRepository) {
        this.tomadorRepository = tomadorRepository;
    }

    @Transactional(readOnly = true)
    public Page<Tomador> executar(Pageable pageable) {
        return tomadorRepository.listar(pageable);
    }
}
