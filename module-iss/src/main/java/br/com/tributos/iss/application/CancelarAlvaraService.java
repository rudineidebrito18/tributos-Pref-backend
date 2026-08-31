package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Alvara;
import br.com.tributos.iss.domain.AlvaraRepository;
import br.com.tributos.iss.domain.SituacaoFiscalAlvara;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class CancelarAlvaraService {

    private final AlvaraRepository alvaraRepository;

    public CancelarAlvaraService(AlvaraRepository alvaraRepository) {
        this.alvaraRepository = alvaraRepository;
    }

    @Transactional
    public Alvara executar(UUID alvaraId, String motivoCancelamento) {
        Alvara alvara = alvaraRepository.buscarPorId(alvaraId)
            .orElseThrow(() -> new NotFoundException("Alvará não encontrado."));

        if (alvara.situacaoFiscal() == SituacaoFiscalAlvara.CANCELADA) {
            throw new ValidationException("O alvará já está cancelado.");
        }

        String motivo = motivoCancelamento != null ? motivoCancelamento.trim() : "";
        if (motivo.isEmpty()) {
            throw new ValidationException("Informe o motivo do cancelamento.");
        }

        return alvaraRepository.salvar(copiar(alvara, SituacaoFiscalAlvara.CANCELADA, motivo));
    }

    static Alvara copiar(Alvara alvara, SituacaoFiscalAlvara situacao, String motivoCancelamento) {
        return new Alvara(
            alvara.id(),
            alvara.tenantId(),
            alvara.numero(),
            alvara.tipoAlvaraId(),
            alvara.contribuinteId(),
            alvara.dataExpedicao(),
            alvara.validade(),
            situacao,
            alvara.valor(),
            alvara.codigoVerificacao(),
            alvara.dataEmissao(),
            alvara.valorPorUnidade(),
            alvara.unidadeMedidaDescritivo(),
            alvara.qtdUnidadeMedida(),
            alvara.documentoHtml(),
            alvara.responsavelTecnico(),
            alvara.inscricaoConselhoRt(),
            motivoCancelamento,
            alvara.observacao()
        );
    }
}
