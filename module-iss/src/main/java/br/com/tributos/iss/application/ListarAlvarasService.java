package br.com.tributos.iss.application;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Alvara;
import br.com.tributos.iss.domain.AlvaraRepository;

@Service
public class ListarAlvarasService {

    private final AlvaraRepository alvaraRepository;

    public ListarAlvarasService(AlvaraRepository alvaraRepository) {
        this.alvaraRepository = alvaraRepository;
    }

    @Transactional(readOnly = true)
    public Page<Alvara> executar(UUID contribuinteId, Pageable pageable) {
        return alvaraRepository.listar(contribuinteId, pageable);
    }
}
