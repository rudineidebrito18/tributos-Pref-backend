package br.com.tributos.iptu.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelRepository;

@Service
public class ImportarImovelLegadoService {

    private final SalvarImovelService salvarImovelService;
    private final ImovelRepository imovelRepository;

    public ImportarImovelLegadoService(
        SalvarImovelService salvarImovelService,
        ImovelRepository imovelRepository
    ) {
        this.salvarImovelService = salvarImovelService;
        this.imovelRepository = imovelRepository;
    }

    @Transactional
    public List<Imovel> executar(List<SalvarImovelComando> itens) {
        if (itens == null || itens.isEmpty()) {
            return List.of();
        }

        List<Imovel> importados = new ArrayList<>();
        for (SalvarImovelComando item : itens) {
            if (item.codigoLegado() == null || item.codigoLegado().isBlank()) {
                continue;
            }
            String codigo = item.codigoLegado().trim();
            if (imovelRepository.buscarPorCodigoLegado(codigo).isPresent()) {
                continue;
            }
            importados.add(salvarImovelService.executar(item, null));
        }
        return importados;
    }
}
