package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.AtividadeServico;
import br.com.tributos.iss.domain.AtividadeServicoRepository;
import br.com.tributos.kernel.exception.RegraNegocioException;

@Service
public class BuscarAliquotaVigenteService {

    private final AtividadeServicoRepository atividadeServicoRepository;

    public BuscarAliquotaVigenteService(AtividadeServicoRepository atividadeServicoRepository) {
        this.atividadeServicoRepository = atividadeServicoRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal executar(UUID atividadeId, UUID servicoId) {
        AtividadeServico vinculo = atividadeServicoRepository.buscarPorAtividadeEServico(atividadeId, servicoId)
            .orElseThrow(() -> new RegraNegocioException(
                "Não há alíquota cadastrada no catálogo para o par atividade/serviço informado."
            ));
        return vinculo.aliquota();
    }
}
