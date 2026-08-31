package br.com.tributos.financeiro.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.tributos.financeiro.domain.FormaPagamentoRepository;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class ListarGuiasService {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;

    public ListarGuiasService(
        GuiaArrecadacaoRepository guiaArrecadacaoRepository,
        FormaPagamentoRepository formaPagamentoRepository
    ) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
        this.formaPagamentoRepository = formaPagamentoRepository;
    }

    public Page<GuiaArrecadacao> executar(
        TipoTributo tipoTributo,
        SituacaoGuia situacao,
        UUID contribuinteId,
        StatusPix statusPix,
        String formaPagamentoCodigo,
        Pageable pageable
    ) {
        UUID formaPagamentoId = null;
        if (formaPagamentoCodigo != null && !formaPagamentoCodigo.isBlank()) {
            formaPagamentoId = formaPagamentoRepository.buscarPorCodigo(formaPagamentoCodigo)
                .map(f -> f.id())
                .orElseThrow(() -> new ValidationException("Forma de pagamento inválida."));
        }
        return guiaArrecadacaoRepository.listar(
            tipoTributo, situacao, contribuinteId, statusPix, formaPagamentoId, pageable
        );
    }
}
