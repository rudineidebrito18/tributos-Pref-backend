package br.com.tributos.itbi.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.itbi.domain.GuiaItbi;
import br.com.tributos.itbi.domain.GuiaItbiRepository;
import br.com.tributos.itbi.domain.NaturezaTransmissaoRepository;
import br.com.tributos.itbi.domain.SituacaoGuiaItbi;
import br.com.tributos.itbi.domain.TipoGuiaItbiRepository;
import br.com.tributos.itbi.domain.TipoTributacaoItbi;
import br.com.tributos.kernel.events.GuiaItbiSolicitadaEvent;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.iptu.ImovelItbiPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class SolicitarGuiaItbiService {

    private static final BigDecimal PERCENTUAL_PADRAO = new BigDecimal("100");

    private final GuiaItbiRepository guiaItbiRepository;
    private final TipoGuiaItbiRepository tipoGuiaItbiRepository;
    private final NaturezaTransmissaoRepository naturezaTransmissaoRepository;
    private final ImovelItbiPort imovelItbiPort;
    private final ApplicationEventPublisher eventPublisher;

    public SolicitarGuiaItbiService(
        GuiaItbiRepository guiaItbiRepository,
        TipoGuiaItbiRepository tipoGuiaItbiRepository,
        NaturezaTransmissaoRepository naturezaTransmissaoRepository,
        ImovelItbiPort imovelItbiPort,
        ApplicationEventPublisher eventPublisher
    ) {
        this.guiaItbiRepository = guiaItbiRepository;
        this.tipoGuiaItbiRepository = tipoGuiaItbiRepository;
        this.naturezaTransmissaoRepository = naturezaTransmissaoRepository;
        this.imovelItbiPort = imovelItbiPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public GuiaItbi executar(SolicitarGuiaItbiComando comando) {
        if (comando.valorTransacao() == null || comando.valorTransacao().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Informe o valor da transação.");
        }

        var tipoGuia = tipoGuiaItbiRepository.buscarPorId(comando.tipoGuiaId())
            .orElseThrow(() -> new ValidationException("Tipo de guia ITBI inválido."));
        if (!tipoGuia.ativo()) {
            throw new ValidationException("Tipo de guia ITBI inativo.");
        }

        naturezaTransmissaoRepository.buscarPorId(comando.naturezaTransmissaoId())
            .filter(n -> n.ativo())
            .orElseThrow(() -> new ValidationException("Natureza de transmissão inválida."));

        var imovel = imovelItbiPort.buscarDados(comando.imovelId());
        if (!imovel.ativo()) {
            throw new ValidationException("O imóvel precisa estar ativo para solicitar ITBI.");
        }

        if (comando.adquirentePessoaId() != null && comando.adquirentePessoaId().equals(imovel.proprietarioId())) {
            throw new ValidationException("O adquirente deve ser diferente do proprietário atual.");
        }

        BigDecimal percentualTransmitido = PERCENTUAL_PADRAO;
        BigDecimal desconto = BigDecimal.ZERO;
        TipoTributacaoItbi tipoTributacao = TipoTributacaoItbi.TRIBUTAVEL;
        BigDecimal base = CalculadorItbi.calcularBase(
            comando.valorTransacao(), imovel.valorVenalReferencia(), percentualTransmitido
        );
        BigDecimal valorItbi = CalculadorItbi.calcularValorItbi(base, tipoGuia.aliquota(), desconto, tipoTributacao);

        UUID tenantId = TenantContext.getObrigatorio();
        Instant agora = Instant.now();

        GuiaItbi guia = new GuiaItbi(
            UUID.randomUUID(),
            tenantId,
            guiaItbiRepository.proximoNumero(),
            comando.imovelId(),
            comando.tipoGuiaId(),
            comando.naturezaTransmissaoId(),
            agora,
            comando.valorTransacao(),
            imovel.valorVenalReferencia(),
            base,
            tipoGuia.aliquota(),
            valorItbi,
            SituacaoGuiaItbi.AGUARDANDO_PAGAMENTO,
            false,
            null,
            percentualTransmitido,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            desconto,
            tipoTributacao,
            null,
            null,
            null
        );

        GuiaItbi salva = guiaItbiRepository.salvar(guia);

        eventPublisher.publishEvent(new GuiaItbiSolicitadaEvent(
            salva.id(),
            salva.tenantId(),
            comando.adquirentePessoaId(),
            salva.valorItbi(),
            salva.dataSolicitacao()
        ));

        return salva;
    }

    public record SolicitarGuiaItbiComando(
        UUID imovelId,
        UUID adquirentePessoaId,
        UUID tipoGuiaId,
        UUID naturezaTransmissaoId,
        BigDecimal valorTransacao
    ) {
    }
}
