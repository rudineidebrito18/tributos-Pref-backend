package br.com.tributos.iss.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AtividadeServicoRepository {

    AtividadeServico salvar(AtividadeServico atividadeServico);

    Optional<AtividadeServico> buscarPorId(UUID id);

    Optional<AtividadeServico> buscarPorAtividadeEServico(UUID atividadeId, UUID servicoId);

    boolean existePorAtividadeEServico(UUID atividadeId, UUID servicoId, UUID ignorarId);

    Page<AtividadeServico> listar(String codigoCnae, String codigoServico, Pageable pageable);

    Page<AtividadeServicoView> listarView(String codigoCnae, String codigoServico, Pageable pageable);

    record AtividadeServicoView(
        UUID id,
        String cnae,
        String codigo,
        String servico,
        java.math.BigDecimal aliquota,
        boolean tributavel,
        boolean deducao,
        boolean retencao,
        String incidencia
    ) {
    }
}
