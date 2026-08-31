package br.com.tributos.iss.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.adapters.out.persistence.UsuarioReferenciaJpaRepository;
import br.com.tributos.iss.domain.Solicitacao;
import br.com.tributos.iss.domain.SolicitacaoRepository;
import br.com.tributos.iss.domain.StatusSolicitacaoRepository;
import br.com.tributos.iss.domain.TipoSolicitacaoRepository;

@Service
public class ListarSolicitacoesService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final TipoSolicitacaoRepository tipoSolicitacaoRepository;
    private final StatusSolicitacaoRepository statusSolicitacaoRepository;
    private final UsuarioReferenciaJpaRepository usuarioReferenciaJpaRepository;

    public ListarSolicitacoesService(
        SolicitacaoRepository solicitacaoRepository,
        TipoSolicitacaoRepository tipoSolicitacaoRepository,
        StatusSolicitacaoRepository statusSolicitacaoRepository,
        UsuarioReferenciaJpaRepository usuarioReferenciaJpaRepository
    ) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.tipoSolicitacaoRepository = tipoSolicitacaoRepository;
        this.statusSolicitacaoRepository = statusSolicitacaoRepository;
        this.usuarioReferenciaJpaRepository = usuarioReferenciaJpaRepository;
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoListagemItem> executar(UUID tipoId, UUID statusId, Pageable pageable) {
        return solicitacaoRepository.listar(tipoId, statusId, pageable).map(this::paraItem);
    }

    private SolicitacaoListagemItem paraItem(Solicitacao solicitacao) {
        String usuario = usuarioReferenciaJpaRepository.findById(solicitacao.usuarioId())
            .map(u -> u.getLogin())
            .orElse("—");

        String tipo = tipoSolicitacaoRepository.buscarPorId(solicitacao.tipoSolicitacaoId())
            .map(t -> t.descricao())
            .orElse("—");

        String status = statusSolicitacaoRepository.buscarPorId(solicitacao.statusSolicitacaoId())
            .map(s -> s.descricao())
            .orElse("—");

        return new SolicitacaoListagemItem(
            solicitacao.id(),
            usuario,
            solicitacao.descricao(),
            tipo,
            status,
            solicitacao.dataHora()
        );
    }

    public record SolicitacaoListagemItem(
        UUID id,
        String usuario,
        String descricao,
        String tipoSolicitacao,
        String status,
        Instant data
    ) {
    }
}
