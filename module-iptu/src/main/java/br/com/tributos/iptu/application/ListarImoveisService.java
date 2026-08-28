package br.com.tributos.iptu.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelRepository;

@Service
public class ListarImoveisService {

    private final ImovelRepository imovelRepository;

    public ListarImoveisService(ImovelRepository imovelRepository) {
        this.imovelRepository = imovelRepository;
    }

    @Transactional(readOnly = true)
    public Page<Imovel> executar(String busca, Pageable pageable) {
        return imovelRepository.listar(busca, pageable);
    }
}
