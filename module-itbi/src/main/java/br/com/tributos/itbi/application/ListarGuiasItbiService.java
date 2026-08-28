package br.com.tributos.itbi.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.tributos.itbi.domain.GuiaItbi;
import br.com.tributos.itbi.domain.GuiaItbiRepository;

@Service
public class ListarGuiasItbiService {

    private final GuiaItbiRepository guiaItbiRepository;

    public ListarGuiasItbiService(GuiaItbiRepository guiaItbiRepository) {
        this.guiaItbiRepository = guiaItbiRepository;
    }

    public Page<GuiaItbi> executar(UUID imovelId, Pageable pageable) {
        return guiaItbiRepository.listar(imovelId, pageable);
    }
}
