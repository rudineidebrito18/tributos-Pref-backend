package br.com.tributos.iss.domain;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotaFiscalRepository {

    NotaFiscal salvar(NotaFiscal notaFiscal);

    Optional<NotaFiscal> buscarPorId(UUID id);

    Page<NotaFiscal> listar(UUID contribuinteId, UUID tomadorId, LocalDate competencia, Pageable pageable);

    long proximoNumero();
}
