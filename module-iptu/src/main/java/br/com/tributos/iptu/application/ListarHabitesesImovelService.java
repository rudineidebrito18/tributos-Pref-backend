package br.com.tributos.iptu.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.HabiteseImovel;
import br.com.tributos.iptu.domain.HabiteseImovelRepository;

@Service
public class ListarHabitesesImovelService {

    private final HabiteseImovelRepository habiteseImovelRepository;

    public ListarHabitesesImovelService(HabiteseImovelRepository habiteseImovelRepository) {
        this.habiteseImovelRepository = habiteseImovelRepository;
    }

    @Transactional(readOnly = true)
    public Page<HabiteseImovel> executar(UUID imovelId, Pageable pageable) {
        return habiteseImovelRepository.listarPorImovel(imovelId, pageable);
    }
}
