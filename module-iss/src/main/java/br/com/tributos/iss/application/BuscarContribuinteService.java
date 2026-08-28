package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BuscarContribuinteService {

    private final ContribuinteRepository contribuinteRepository;

    public BuscarContribuinteService(ContribuinteRepository contribuinteRepository) {
        this.contribuinteRepository = contribuinteRepository;
    }

    @Transactional(readOnly = true)
    public Contribuinte executar(UUID id) {
        return contribuinteRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));
    }
}
