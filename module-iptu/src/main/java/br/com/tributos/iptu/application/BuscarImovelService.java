package br.com.tributos.iptu.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BuscarImovelService {

    private final ImovelRepository imovelRepository;

    public BuscarImovelService(ImovelRepository imovelRepository) {
        this.imovelRepository = imovelRepository;
    }

    @Transactional(readOnly = true)
    public Imovel executar(UUID id) {
        return imovelRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Imóvel não encontrado."));
    }
}
