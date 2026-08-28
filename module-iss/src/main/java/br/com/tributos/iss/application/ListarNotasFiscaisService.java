package br.com.tributos.iss.application;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.NotaFiscal;
import br.com.tributos.iss.domain.NotaFiscalRepository;

@Service
public class ListarNotasFiscaisService {

    private final NotaFiscalRepository notaFiscalRepository;

    public ListarNotasFiscaisService(NotaFiscalRepository notaFiscalRepository) {
        this.notaFiscalRepository = notaFiscalRepository;
    }

    @Transactional(readOnly = true)
    public Page<NotaFiscal> executar(UUID contribuinteId, UUID tomadorId, YearMonth competencia, Pageable pageable) {
        LocalDate competenciaFiltro = competencia != null ? competencia.atDay(1) : null;
        return notaFiscalRepository.listar(contribuinteId, tomadorId, competenciaFiltro, pageable);
    }
}
