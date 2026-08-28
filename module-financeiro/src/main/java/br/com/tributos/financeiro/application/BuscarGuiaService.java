package br.com.tributos.financeiro.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BuscarGuiaService {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;

    public BuscarGuiaService(GuiaArrecadacaoRepository guiaArrecadacaoRepository) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
    }

    public GuiaArrecadacao executar(UUID id) {
        return guiaArrecadacaoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));
    }
}
