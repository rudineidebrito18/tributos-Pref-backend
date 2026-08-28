package br.com.tributos.application.portal;

import org.springframework.stereotype.Service;

import br.com.tributos.cadastro.domain.PessoaRepository;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.vo.CpfCnpj;

@Service
public class SegundaViaGuiaPublicaService {

    private final PessoaRepository pessoaRepository;
    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;

    public SegundaViaGuiaPublicaService(
        PessoaRepository pessoaRepository,
        GuiaArrecadacaoRepository guiaArrecadacaoRepository
    ) {
        this.pessoaRepository = pessoaRepository;
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
    }

    public SegundaViaGuiaPublica executar(long numeroGuia, String cpfCnpj) {
        CpfCnpj.de(cpfCnpj);
        var pessoa = pessoaRepository.buscarPorCpfCnpj(cpfCnpj)
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));

        GuiaArrecadacao guia = guiaArrecadacaoRepository.buscarPorNumero(numeroGuia)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));

        if (!guia.contribuinteId().equals(pessoa.getId())) {
            throw new ValidationException("O documento informado não corresponde ao titular da guia.");
        }

        return new SegundaViaGuiaPublica(
            guia.numero(),
            guia.tipoTributo().name(),
            guia.valor(),
            guia.dataVencimento(),
            guia.situacao().name(),
            guia.codigoBarras(),
            guia.pixTxid()
        );
    }

    public record SegundaViaGuiaPublica(
        long numero,
        String tipoTributo,
        java.math.BigDecimal valor,
        java.time.LocalDate dataVencimento,
        String situacao,
        String codigoBarras,
        String pixTxid
    ) {
    }
}
