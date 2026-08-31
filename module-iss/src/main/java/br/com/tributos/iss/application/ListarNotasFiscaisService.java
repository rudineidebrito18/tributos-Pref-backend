package br.com.tributos.iss.application;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.NotaFiscal;
import br.com.tributos.iss.domain.NotaFiscalRepository;
import br.com.tributos.iss.domain.PessoaDadosResumo;
import br.com.tributos.iss.domain.PessoaReferenciaRepository;

@Service
public class ListarNotasFiscaisService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final ContribuinteRepository contribuinteRepository;
    private final PessoaReferenciaRepository pessoaReferenciaRepository;

    public ListarNotasFiscaisService(
        NotaFiscalRepository notaFiscalRepository,
        ContribuinteRepository contribuinteRepository,
        PessoaReferenciaRepository pessoaReferenciaRepository
    ) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.contribuinteRepository = contribuinteRepository;
        this.pessoaReferenciaRepository = pessoaReferenciaRepository;
    }

    @Transactional(readOnly = true)
    public Page<NotaFiscalListagemItem> executar(UUID contribuinteId, UUID tomadorId, YearMonth competencia, Pageable pageable) {
        LocalDate competenciaFiltro = competencia != null ? competencia.atDay(1) : null;
        return notaFiscalRepository.listar(contribuinteId, tomadorId, competenciaFiltro, pageable)
            .map(this::paraItem);
    }

    private NotaFiscalListagemItem paraItem(NotaFiscal nota) {
        String contribuinte = contribuinteRepository.buscarPorId(nota.contribuinteId())
            .flatMap(c -> pessoaReferenciaRepository.buscarDados(c.pessoaId()))
            .map(PessoaDadosResumo::nome)
            .orElse("—");

        return new NotaFiscalListagemItem(
            nota.id(),
            nota.numero(),
            nota.status().name(),
            contribuinte,
            nota.dataEmissao(),
            nota.valorServico(),
            nota.valorIss()
        );
    }

    public record NotaFiscalListagemItem(
        UUID id,
        long numero,
        String situacao,
        String contribuinte,
        java.time.Instant dataEmissao,
        java.math.BigDecimal valor,
        java.math.BigDecimal valorIss
    ) {
    }
}
