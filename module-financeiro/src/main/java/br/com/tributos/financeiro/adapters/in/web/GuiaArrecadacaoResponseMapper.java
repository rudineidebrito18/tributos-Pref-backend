package br.com.tributos.financeiro.adapters.in.web;

import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.adapters.in.web.dto.GuiaArrecadacaoResponse;
import br.com.tributos.financeiro.domain.FormaPagamentoRepository;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;

@Component
public class GuiaArrecadacaoResponseMapper {

    private final FormaPagamentoRepository formaPagamentoRepository;

    public GuiaArrecadacaoResponseMapper(FormaPagamentoRepository formaPagamentoRepository) {
        this.formaPagamentoRepository = formaPagamentoRepository;
    }

    public GuiaArrecadacaoResponse paraResponse(GuiaArrecadacao guia) {
        String formaPagamentoCodigo = null;
        if (guia.formaPagamentoId() != null) {
            formaPagamentoCodigo = formaPagamentoRepository.buscarPorId(guia.formaPagamentoId())
                .map(f -> f.codigo())
                .orElse(null);
        }
        return GuiaArrecadacaoResponse.de(guia, formaPagamentoCodigo);
    }
}
