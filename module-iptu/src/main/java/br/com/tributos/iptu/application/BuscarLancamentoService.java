package br.com.tributos.iptu.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.LancamentoIptu;
import br.com.tributos.iptu.domain.LancamentoIptuRepository;
import br.com.tributos.iptu.domain.LancamentoParcela;
import br.com.tributos.iptu.domain.LancamentoParcelaRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BuscarLancamentoService {

    private final LancamentoIptuRepository lancamentoIptuRepository;
    private final LancamentoParcelaRepository lancamentoParcelaRepository;

    public BuscarLancamentoService(
        LancamentoIptuRepository lancamentoIptuRepository,
        LancamentoParcelaRepository lancamentoParcelaRepository
    ) {
        this.lancamentoIptuRepository = lancamentoIptuRepository;
        this.lancamentoParcelaRepository = lancamentoParcelaRepository;
    }

    @Transactional(readOnly = true)
    public LancamentoComParcelas executar(UUID id) {
        LancamentoIptu lancamento = lancamentoIptuRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Lançamento não encontrado."));
        List<LancamentoParcela> parcelas = lancamentoParcelaRepository.listarPorLancamento(id);
        return new LancamentoComParcelas(lancamento, parcelas);
    }

    public record LancamentoComParcelas(LancamentoIptu lancamento, List<LancamentoParcela> parcelas) {
    }
}
