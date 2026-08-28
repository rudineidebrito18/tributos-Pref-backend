package br.com.tributos.financeiro.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.TipoTributo;

@Service
public class ListarGuiasService {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;

    public ListarGuiasService(GuiaArrecadacaoRepository guiaArrecadacaoRepository) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
    }

    public Page<GuiaArrecadacao> executar(
        TipoTributo tipoTributo,
        SituacaoGuia situacao,
        UUID contribuinteId,
        Pageable pageable
    ) {
        return guiaArrecadacaoRepository.listar(tipoTributo, situacao, contribuinteId, pageable);
    }
}
