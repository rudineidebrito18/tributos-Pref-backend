package br.com.tributos.iss.application;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.CertidaoIss;
import br.com.tributos.iss.domain.CertidaoIssRepository;

@Service
public class ListarCertidoesService {

    private final CertidaoIssRepository certidaoIssRepository;

    public ListarCertidoesService(CertidaoIssRepository certidaoIssRepository) {
        this.certidaoIssRepository = certidaoIssRepository;
    }

    @Transactional(readOnly = true)
    public Page<CertidaoIss> executar(UUID contribuinteId, Pageable pageable) {
        return certidaoIssRepository.listar(contribuinteId, pageable);
    }
}
