package br.com.tributos.financeiro.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.events.LancamentoIptuParcelaGeradaEvent;
import br.com.tributos.kernel.events.NotaFiscalEmitidaEvent;

@Component
public class GerarGuiaEventListener {

    private final GerarGuiaArrecadacaoService gerarGuiaArrecadacaoService;

    public GerarGuiaEventListener(GerarGuiaArrecadacaoService gerarGuiaArrecadacaoService) {
        this.gerarGuiaArrecadacaoService = gerarGuiaArrecadacaoService;
    }

    @EventListener
    @Transactional
    public void onNotaFiscalEmitida(NotaFiscalEmitidaEvent evento) {
        gerarGuiaArrecadacaoService.executar(new GerarGuiaArrecadacaoService.GerarGuiaComando(
            TipoTributo.ISS,
            OrigemGuia.NOTA_FISCAL,
            evento.notaId(),
            evento.contribuintePessoaId(),
            evento.competencia().getMonthValue(),
            evento.competencia().getYear(),
            evento.dataEmissao(),
            evento.competencia().plusMonths(1).withDayOfMonth(10),
            evento.valorIss(),
            null
        ));
    }

    @EventListener
    @Transactional
    public void onParcelaIptuGerada(LancamentoIptuParcelaGeradaEvent evento) {
        gerarGuiaArrecadacaoService.executar(new GerarGuiaArrecadacaoService.GerarGuiaComando(
            TipoTributo.IPTU,
            OrigemGuia.LANCAMENTO_IPTU_PARCELA,
            evento.parcelaId(),
            evento.proprietarioPessoaId(),
            null,
            evento.exercicio(),
            null,
            evento.vencimento(),
            evento.valor(),
            "IPTU " + evento.exercicio() + " — parcela " + evento.numeroParcela()
        ));
    }
}
