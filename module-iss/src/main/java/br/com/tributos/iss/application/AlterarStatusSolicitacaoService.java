package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Solicitacao;
import br.com.tributos.iss.domain.SolicitacaoRepository;
import br.com.tributos.iss.domain.StatusSolicitacaoRepository;
import br.com.tributos.iss.domain.TipoSolicitacao;
import br.com.tributos.iss.domain.TipoSolicitacaoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.identity.MensageriaInternaPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class AlterarStatusSolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final StatusSolicitacaoRepository statusSolicitacaoRepository;
    private final TipoSolicitacaoRepository tipoSolicitacaoRepository;
    private final MensageriaInternaPort mensageriaInternaPort;

    public AlterarStatusSolicitacaoService(
        SolicitacaoRepository solicitacaoRepository,
        StatusSolicitacaoRepository statusSolicitacaoRepository,
        TipoSolicitacaoRepository tipoSolicitacaoRepository,
        MensageriaInternaPort mensageriaInternaPort
    ) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.statusSolicitacaoRepository = statusSolicitacaoRepository;
        this.tipoSolicitacaoRepository = tipoSolicitacaoRepository;
        this.mensageriaInternaPort = mensageriaInternaPort;
    }

    @Transactional
    public Solicitacao executar(UUID solicitacaoId, UUID novoStatusId) {
        UUID tenantId = TenantContext.getObrigatorio();

        Solicitacao existente = solicitacaoRepository.buscarPorId(solicitacaoId)
            .filter(s -> s.tenantId().equals(tenantId))
            .orElseThrow(() -> new NotFoundException("Solicitação não encontrada."));

        var novoStatus = statusSolicitacaoRepository.buscarPorId(novoStatusId)
            .orElseThrow(() -> new NotFoundException("Status de solicitação não encontrado."));

        Solicitacao atualizada = new Solicitacao(
            existente.id(),
            existente.tenantId(),
            existente.usuarioId(),
            existente.tipoSolicitacaoId(),
            novoStatusId,
            existente.descricao(),
            existente.dataHora(),
            existente.criadoEm()
        );
        Solicitacao salva = solicitacaoRepository.salvar(atualizada);

        TipoSolicitacao tipo = tipoSolicitacaoRepository.buscarPorId(existente.tipoSolicitacaoId()).orElse(null);
        if (tipo != null && tipo.usuarioNotificarId() != null) {
            mensageriaInternaPort.enviar(
                tipo.usuarioNotificarId(),
                "Solicitação atualizada: " + tipo.descricao(),
                "Status alterado para: " + novoStatus.descricao() + "\n\n" + existente.descricao()
            );
        }

        return salva;
    }
}
