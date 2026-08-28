package br.com.tributos.iss.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;

@Service
public class ListarContribuintesService {

    private final ContribuinteRepository contribuinteRepository;

    public ListarContribuintesService(ContribuinteRepository contribuinteRepository) {
        this.contribuinteRepository = contribuinteRepository;
    }

    @Transactional(readOnly = true)
    public Page<Contribuinte> executar(String busca, Pageable pageable) {
        return contribuinteRepository.listar(busca, pageable);
    }
}
