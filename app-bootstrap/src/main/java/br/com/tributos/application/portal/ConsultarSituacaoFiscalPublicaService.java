package br.com.tributos.application.portal;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.cadastro.domain.Pessoa;
import br.com.tributos.cadastro.domain.PessoaRepository;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.financeiro.PendenciaFinanceiraPort;
import br.com.tributos.kernel.tenancy.TenantContext;
import br.com.tributos.kernel.vo.CpfCnpj;

@Service
public class ConsultarSituacaoFiscalPublicaService {

    private final PessoaRepository pessoaRepository;
    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    private final PendenciaFinanceiraPort pendenciaFinanceiraPort;

    public ConsultarSituacaoFiscalPublicaService(
        PessoaRepository pessoaRepository,
        GuiaArrecadacaoRepository guiaArrecadacaoRepository,
        PendenciaFinanceiraPort pendenciaFinanceiraPort
    ) {
        this.pessoaRepository = pessoaRepository;
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
        this.pendenciaFinanceiraPort = pendenciaFinanceiraPort;
    }

    public SituacaoFiscalPublica executar(String cpfCnpj) {
        CpfCnpj.de(cpfCnpj);
        Pessoa pessoa = pessoaRepository.buscarPorCpfCnpj(cpfCnpj)
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado para o documento informado."));

        UUID tenantId = TenantContext.getObrigatorio();
        var guiasPendentes = guiaArrecadacaoRepository.listar(
            null, SituacaoGuia.PENDENTE, pessoa.getId(), null, null,
            org.springframework.data.domain.PageRequest.of(0, 100)
        );

        boolean possuiPendencia = pendenciaFinanceiraPort.possuiPendencia(tenantId, pessoa.getId());

        return new SituacaoFiscalPublica(
            pessoa.getNome(),
            possuiPendencia,
            guiasPendentes.getTotalElements()
        );
    }

    public record SituacaoFiscalPublica(String nomeContribuinte, boolean possuiPendencia, long guiasPendentes) {
    }
}
