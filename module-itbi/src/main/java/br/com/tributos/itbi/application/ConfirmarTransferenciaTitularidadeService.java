package br.com.tributos.itbi.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.itbi.domain.GuiaItbi;
import br.com.tributos.itbi.domain.GuiaItbiRepository;
import br.com.tributos.itbi.domain.SituacaoGuiaItbi;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.financeiro.GuiaFinanceiraConsultaPort;
import br.com.tributos.kernel.iptu.ImovelItbiPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class ConfirmarTransferenciaTitularidadeService {

    private static final String ORIGEM_ITBI = "ITBI_GUIA";

    private final GuiaItbiRepository guiaItbiRepository;
    private final ImovelItbiPort imovelItbiPort;
    private final GuiaFinanceiraConsultaPort guiaFinanceiraConsultaPort;

    public ConfirmarTransferenciaTitularidadeService(
        GuiaItbiRepository guiaItbiRepository,
        ImovelItbiPort imovelItbiPort,
        GuiaFinanceiraConsultaPort guiaFinanceiraConsultaPort
    ) {
        this.guiaItbiRepository = guiaItbiRepository;
        this.imovelItbiPort = imovelItbiPort;
        this.guiaFinanceiraConsultaPort = guiaFinanceiraConsultaPort;
    }

    @Transactional
    public GuiaItbi executar(UUID guiaItbiId) {
        GuiaItbi guia = guiaItbiRepository.buscarPorId(guiaItbiId)
            .orElseThrow(() -> new NotFoundException("Guia ITBI não encontrada."));

        if (guia.transferenciaTitularidadeRealizada()) {
            throw new ValidationException("A transferência de titularidade já foi confirmada.");
        }

        if (guia.situacao() == SituacaoGuiaItbi.CANCELADA) {
            throw new ValidationException("Guia ITBI cancelada.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        if (!guiaFinanceiraConsultaPort.origemEstaPaga(tenantId, ORIGEM_ITBI, guia.id())) {
            throw new ValidationException("A guia de arrecadação do ITBI ainda não foi paga.");
        }

        imovelItbiPort.transferirTitularidade(guia.imovelId(), guia.adquirenteId());

        GuiaItbi atualizada = new GuiaItbi(
            guia.id(), guia.tenantId(), guia.numero(), guia.imovelId(), guia.adquirenteId(),
            guia.tipoGuiaId(), guia.naturezaTransmissaoId(), guia.dataSolicitacao(),
            guia.valorTransacao(), guia.valorVenalReferencia(), guia.baseCalculo(), guia.aliquota(),
            guia.valorItbi(), SituacaoGuiaItbi.TRANSFERENCIA_REALIZADA, true
        );

        return guiaItbiRepository.salvar(atualizada);
    }
}
