package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class EmitirDamAvulsoService {

    private final GerarGuiaArrecadacaoService gerarGuiaArrecadacaoService;

    public EmitirDamAvulsoService(GerarGuiaArrecadacaoService gerarGuiaArrecadacaoService) {
        this.gerarGuiaArrecadacaoService = gerarGuiaArrecadacaoService;
    }

    @Transactional
    public GuiaArrecadacao executar(UUID contribuintePessoaId, BigDecimal valor, LocalDate vencimento, String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new ValidationException("Informe a descrição do DAM avulso.");
        }

        return gerarGuiaArrecadacaoService.executar(new GerarGuiaArrecadacaoService.GerarGuiaComando(
            TipoTributo.OUTROS,
            OrigemGuia.AVULSO,
            null,
            contribuintePessoaId,
            null,
            null,
            null,
            vencimento,
            valor,
            descricao.trim()
        ));
    }
}
