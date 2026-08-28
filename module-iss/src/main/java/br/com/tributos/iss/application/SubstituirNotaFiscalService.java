package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.NotaFiscal;
import br.com.tributos.iss.domain.NotaFiscalRepository;
import br.com.tributos.iss.domain.StatusNotaFiscal;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class SubstituirNotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final EmitirNotaFiscalService emitirNotaFiscalService;

    public SubstituirNotaFiscalService(
        NotaFiscalRepository notaFiscalRepository,
        EmitirNotaFiscalService emitirNotaFiscalService
    ) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.emitirNotaFiscalService = emitirNotaFiscalService;
    }

    @Transactional
    public NotaFiscal executar(UUID notaOriginalId, EmitirNotaFiscalComando comando) {
        NotaFiscal notaOriginal = notaFiscalRepository.buscarPorId(notaOriginalId)
            .orElseThrow(() -> new NotFoundException("Nota fiscal não encontrada."));

        if (notaOriginal.status() != StatusNotaFiscal.EMITIDA) {
            throw new ValidationException("Somente notas emitidas podem ser substituídas.");
        }

        EmitirNotaFiscalComando comandoComContribuinte = new EmitirNotaFiscalComando(
            notaOriginal.contribuinteId(),
            comando.tomadorId(),
            comando.servicoId(),
            comando.competencia(),
            comando.valorServico(),
            comando.valorDeducoes(),
            comando.receitaBrutaAcumulada12Meses(),
            comando.serie()
        );

        NotaFiscal notaSubstituta = emitirNotaFiscalService.executar(comandoComContribuinte);

        NotaFiscal notaSubstituida = new NotaFiscal(
            notaOriginal.id(),
            notaOriginal.tenantId(),
            notaOriginal.numero(),
            notaOriginal.serie(),
            notaOriginal.contribuinteId(),
            notaOriginal.tomadorId(),
            notaOriginal.servicoId(),
            notaOriginal.competencia(),
            notaOriginal.valorServico(),
            notaOriginal.valorDeducoes(),
            notaOriginal.baseCalculo(),
            notaOriginal.aliquotaAplicada(),
            notaOriginal.valorIss(),
            StatusNotaFiscal.SUBSTITUIDA,
            notaSubstituta.id(),
            notaOriginal.motivoCancelamento(),
            notaOriginal.dataEmissao()
        );

        notaFiscalRepository.salvar(notaSubstituida);
        return notaSubstituta;
    }
}
