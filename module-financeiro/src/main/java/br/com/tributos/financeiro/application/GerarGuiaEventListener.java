package br.com.tributos.financeiro.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.TipoTributacao;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.events.GuiaItbiSolicitadaEvent;
import br.com.tributos.kernel.events.HabiteseEmitidoEvent;
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
            null,
            TipoTributacao.TRIBUTAVEL
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
            "IPTU " + evento.exercicio() + " — parcela " + evento.numeroParcela(),
            TipoTributacao.TRIBUTAVEL
        ));
    }

    @EventListener
    @Transactional
    public void onGuiaItbiSolicitada(GuiaItbiSolicitadaEvent evento) {
        gerarGuiaArrecadacaoService.executar(new GerarGuiaArrecadacaoService.GerarGuiaComando(
            TipoTributo.ITBI,
            OrigemGuia.ITBI_GUIA,
            evento.guiaItbiId(),
            evento.adquirentePessoaId(),
            null,
            null,
            evento.dataSolicitacao(),
            java.time.LocalDate.ofInstant(evento.dataSolicitacao(), java.time.ZoneId.of("America/Sao_Paulo")).plusDays(30),
            evento.valorItbi(),
            "ITBI — guia " + evento.guiaItbiId(),
            TipoTributacao.TRIBUTAVEL
        ));
    }

    @EventListener
    @Transactional
    public void onHabiteseEmitido(HabiteseEmitidoEvent evento) {
        gerarGuiaArrecadacaoService.executar(new GerarGuiaArrecadacaoService.GerarGuiaComando(
            TipoTributo.HABITE_SE,
            OrigemGuia.HABITE_SE,
            evento.habiteseId(),
            evento.contribuintePessoaId(),
            null,
            null,
            evento.dataEmissao(),
            java.time.LocalDate.ofInstant(evento.dataEmissao(), java.time.ZoneId.of("America/Sao_Paulo")).plusDays(30),
            evento.valor(),
            "Habite-se — " + evento.habiteseId(),
            TipoTributacao.TRIBUTAVEL
        ));
    }
}
