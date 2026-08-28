package br.com.tributos.iptu.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.LancamentoIptu;
import br.com.tributos.iptu.domain.LancamentoIptuRepository;

@Service
public class ListarLancamentosService {

    private final LancamentoIptuRepository lancamentoIptuRepository;

    public ListarLancamentosService(LancamentoIptuRepository lancamentoIptuRepository) {
        this.lancamentoIptuRepository = lancamentoIptuRepository;
    }

    @Transactional(readOnly = true)
    public Page<LancamentoIptu> executar(Integer exercicio, UUID imovelId, Pageable pageable) {
        return lancamentoIptuRepository.listar(exercicio, imovelId, pageable);
    }
}
