package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerarGuiaArrecadacaoService {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;

    public GerarGuiaArrecadacaoService(GuiaArrecadacaoRepository guiaArrecadacaoRepository) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
    }

    @Transactional
    public GuiaArrecadacao executar(GerarGuiaComando comando) {
        if (comando.valor() == null || comando.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("O valor da guia deve ser maior que zero.");
        }
        if (comando.contribuintePessoaId() == null) {
            throw new ValidationException("Informe o contribuinte (pessoa) da guia.");
        }

        if (comando.origemTipo() != null && comando.origemId() != null) {
            var existente = guiaArrecadacaoRepository.buscarPorOrigem(comando.origemTipo(), comando.origemId());
            if (existente.isPresent()) {
                return existente.get();
            }
        }

        UUID tenantId = TenantContext.getObrigatorio();
        Instant emissao = comando.dataEmissao() != null ? comando.dataEmissao() : Instant.now();
        LocalDate vencimento = comando.dataVencimento() != null
            ? comando.dataVencimento()
            : LocalDate.ofInstant(emissao, FUSO).plusDays(30);

        GuiaArrecadacao guia = new GuiaArrecadacao(
            UUID.randomUUID(),
            tenantId,
            guiaArrecadacaoRepository.proximoNumero(),
            comando.tipoTributo(),
            comando.origemTipo(),
            comando.origemId(),
            comando.contribuintePessoaId(),
            comando.competenciaMes(),
            comando.competenciaAno(),
            emissao,
            vencimento,
            comando.valor(),
            SituacaoGuia.PENDENTE,
            null,
            null,
            null,
            null,
            null,
            comando.descricaoAvulsa()
        );

        return guiaArrecadacaoRepository.salvar(guia);
    }

    public record GerarGuiaComando(
        TipoTributo tipoTributo,
        OrigemGuia origemTipo,
        UUID origemId,
        UUID contribuintePessoaId,
        Integer competenciaMes,
        Integer competenciaAno,
        Instant dataEmissao,
        LocalDate dataVencimento,
        BigDecimal valor,
        String descricaoAvulsa
    ) {
    }
}
