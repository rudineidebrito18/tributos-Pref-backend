package br.com.tributos.financeiro.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.financeiro.adapters.in.web.dto.PixConciliacaoLogResponse;
import br.com.tributos.financeiro.domain.PixConciliacaoLogRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;

@Service
public class ListarConciliacaoPixLogService {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    private final PixConciliacaoLogRepository pixConciliacaoLogRepository;

    public ListarConciliacaoPixLogService(
        GuiaArrecadacaoRepository guiaArrecadacaoRepository,
        PixConciliacaoLogRepository pixConciliacaoLogRepository
    ) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
        this.pixConciliacaoLogRepository = pixConciliacaoLogRepository;
    }

    public List<PixConciliacaoLogResponse> executar(UUID guiaId) {
        guiaArrecadacaoRepository.buscarPorId(guiaId)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));
        return pixConciliacaoLogRepository.listarPorGuiaId(guiaId).stream()
            .map(PixConciliacaoLogResponse::de)
            .toList();
    }
}
