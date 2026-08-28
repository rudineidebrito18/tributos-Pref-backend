package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class ExcluirContribuinteService {

    private final ContribuinteRepository contribuinteRepository;

    public ExcluirContribuinteService(ContribuinteRepository contribuinteRepository) {
        this.contribuinteRepository = contribuinteRepository;
    }

    @Transactional
    public void executar(UUID id) {
        if (contribuinteRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Contribuinte não encontrado.");
        }
        contribuinteRepository.excluir(id);
    }
}
