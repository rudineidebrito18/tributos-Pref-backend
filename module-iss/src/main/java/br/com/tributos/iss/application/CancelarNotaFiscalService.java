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
public class CancelarNotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;

    public CancelarNotaFiscalService(NotaFiscalRepository notaFiscalRepository) {
        this.notaFiscalRepository = notaFiscalRepository;
    }

    @Transactional
    public NotaFiscal executar(UUID notaId, String motivo) {
        NotaFiscal nota = notaFiscalRepository.buscarPorId(notaId)
            .orElseThrow(() -> new NotFoundException("Nota fiscal não encontrada."));

        if (nota.status() != StatusNotaFiscal.EMITIDA) {
            throw new ValidationException("Somente notas emitidas podem ser canceladas.");
        }

        String motivoCancelamento = motivo != null ? motivo.trim() : "";
        if (motivoCancelamento.isEmpty()) {
            throw new ValidationException("Informe o motivo do cancelamento.");
        }

        NotaFiscal cancelada = copiarComStatus(nota, StatusNotaFiscal.CANCELADA, motivoCancelamento, nota.notaSubstitutaId());
        return notaFiscalRepository.salvar(cancelada);
    }

    static NotaFiscal copiarComStatus(
        NotaFiscal nota,
        StatusNotaFiscal status,
        String motivoCancelamento,
        UUID notaSubstitutaId
    ) {
        return new NotaFiscal(
            nota.id(),
            nota.tenantId(),
            nota.numero(),
            nota.serie(),
            nota.contribuinteId(),
            nota.tomadorId(),
            nota.servicoId(),
            nota.competencia(),
            nota.valorServico(),
            nota.valorDeducoes(),
            nota.baseCalculo(),
            nota.aliquotaAplicada(),
            nota.valorIss(),
            nota.valorIr(),
            nota.valorPis(),
            nota.valorCofins(),
            nota.valorCsll(),
            nota.valorInss(),
            nota.issRetidoFonte(),
            status,
            notaSubstitutaId,
            motivoCancelamento,
            nota.dataEmissao()
        );
    }
}
