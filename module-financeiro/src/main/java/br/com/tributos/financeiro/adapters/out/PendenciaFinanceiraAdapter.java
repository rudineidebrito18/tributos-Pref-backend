package br.com.tributos.financeiro.adapters.out;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.financeiro.PendenciaFinanceiraPort;

@Component
public class PendenciaFinanceiraAdapter implements PendenciaFinanceiraPort {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;

    public PendenciaFinanceiraAdapter(GuiaArrecadacaoRepository guiaArrecadacaoRepository) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
    }

    @Override
    public boolean possuiPendencia(UUID tenantId, UUID pessoaId) {
        return guiaArrecadacaoRepository.possuiPendencia(tenantId, pessoaId);
    }

    @Override
    public boolean possuiPendenciaTributo(UUID tenantId, UUID pessoaId, String codigoTributo) {
        TipoTributo tipoTributo = mapearTributo(codigoTributo);
        if (tipoTributo == null) {
            return false;
        }
        return guiaArrecadacaoRepository.possuiPendenciaTributo(tenantId, pessoaId, tipoTributo);
    }

    private static TipoTributo mapearTributo(String codigoTributo) {
        return switch (codigoTributo) {
            case "ISS" -> TipoTributo.ISS;
            case "IPTU" -> TipoTributo.IPTU;
            case "ITBI" -> TipoTributo.ITBI;
            case "ALVARA" -> TipoTributo.ALVARA;
            default -> null;
        };
    }
}
