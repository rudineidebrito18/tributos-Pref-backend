package br.com.tributos.iss.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Solicitacao;
import br.com.tributos.iss.domain.SolicitacaoRepository;
import br.com.tributos.iss.domain.StatusSolicitacaoRepository;
import br.com.tributos.iss.domain.TipoSolicitacaoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.identity.UsuarioAutenticadoPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class AbrirSolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final TipoSolicitacaoRepository tipoSolicitacaoRepository;
    private final StatusSolicitacaoRepository statusSolicitacaoRepository;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;

    public AbrirSolicitacaoService(
        SolicitacaoRepository solicitacaoRepository,
        TipoSolicitacaoRepository tipoSolicitacaoRepository,
        StatusSolicitacaoRepository statusSolicitacaoRepository,
        UsuarioAutenticadoPort usuarioAutenticadoPort
    ) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.tipoSolicitacaoRepository = tipoSolicitacaoRepository;
        this.statusSolicitacaoRepository = statusSolicitacaoRepository;
        this.usuarioAutenticadoPort = usuarioAutenticadoPort;
    }

    @Transactional
    public Solicitacao executar(UUID tipoSolicitacaoId, UUID statusSolicitacaoId, String descricao, Instant dataHora) {
        if (descricao == null || descricao.isBlank()) {
            throw new ValidationException("Descrição é obrigatória.");
        }
        if (dataHora == null) {
            throw new ValidationException("Data/hora é obrigatória.");
        }

        tipoSolicitacaoRepository.buscarPorId(tipoSolicitacaoId)
            .orElseThrow(() -> new NotFoundException("Tipo de solicitação não encontrado."));
        statusSolicitacaoRepository.buscarPorId(statusSolicitacaoId)
            .orElseThrow(() -> new NotFoundException("Status de solicitação não encontrado."));

        UUID usuarioId = usuarioAutenticadoPort.usuarioIdAtualObrigatorio();
        UUID tenantId = TenantContext.getObrigatorio();

        Solicitacao solicitacao = new Solicitacao(
            UUID.randomUUID(),
            tenantId,
            usuarioId,
            tipoSolicitacaoId,
            statusSolicitacaoId,
            descricao.trim(),
            dataHora,
            null
        );
        return solicitacaoRepository.salvar(solicitacao);
    }
}
