package br.com.tributos.iss.application;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.CatalogoIssRepository;
import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.SolicitacaoCredenciamento;
import br.com.tributos.iss.domain.SolicitacaoCredenciamentoRepository;
import br.com.tributos.iss.domain.StatusCredenciamentoNomes;
import br.com.tributos.iss.domain.TipoCatalogoIss;
import br.com.tributos.kernel.audit.AuditoriaPort;
import br.com.tributos.kernel.audit.RegistroAuditoria;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class AnalisarCredenciamentoService {

    private final SolicitacaoCredenciamentoRepository solicitacaoRepository;
    private final ContribuinteRepository contribuinteRepository;
    private final CatalogoIssRepository catalogoIssRepository;
    private final AuditoriaPort auditoriaPort;

    public AnalisarCredenciamentoService(
        SolicitacaoCredenciamentoRepository solicitacaoRepository,
        ContribuinteRepository contribuinteRepository,
        CatalogoIssRepository catalogoIssRepository,
        AuditoriaPort auditoriaPort
    ) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.contribuinteRepository = contribuinteRepository;
        this.catalogoIssRepository = catalogoIssRepository;
        this.auditoriaPort = auditoriaPort;
    }

    @Transactional
    public SolicitacaoCredenciamento aprovar(UUID solicitacaoId, String observacao) {
        return analisar(solicitacaoId, observacao, StatusCredenciamentoNomes.APROVADO, "APROVAR");
    }

    @Transactional
    public SolicitacaoCredenciamento negar(UUID solicitacaoId, String observacao) {
        return analisar(solicitacaoId, observacao, StatusCredenciamentoNomes.NEGADO, "NEGAR");
    }

    private SolicitacaoCredenciamento analisar(
        UUID solicitacaoId,
        String observacao,
        String statusDestinoNome,
        String acaoAuditoria
    ) {
        SolicitacaoCredenciamento solicitacao = solicitacaoRepository.buscarPorId(solicitacaoId)
            .orElseThrow(() -> new NotFoundException("Solicitação de credenciamento não encontrada."));

        UUID statusEmAnaliseId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.EM_ANALISE)
            .orElseThrow(() -> new IllegalStateException("Status EM_ANALISE não encontrado no catálogo do tenant."))
            .id();

        if (!solicitacao.statusId().equals(statusEmAnaliseId)) {
            throw new ValidationException("A solicitação não está em análise.");
        }

        UUID statusDestinoId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, statusDestinoNome)
            .orElseThrow(() -> new IllegalStateException("Status " + statusDestinoNome + " não encontrado no catálogo do tenant."))
            .id();

        UUID analisadorId = usuarioAutenticadoId();
        Instant analisadoEm = Instant.now();

        Map<String, Object> dadosAntes = Map.of(
            "statusId", solicitacao.statusId(),
            "observacao", solicitacao.observacao() != null ? solicitacao.observacao() : ""
        );

        SolicitacaoCredenciamento atualizada = new SolicitacaoCredenciamento(
            solicitacao.id(),
            solicitacao.tenantId(),
            solicitacao.contribuinteId(),
            statusDestinoId,
            observacao,
            analisadorId,
            analisadoEm,
            solicitacao.criadoEm()
        );
        solicitacaoRepository.salvar(atualizada);

        Contribuinte contribuinte = contribuinteRepository.buscarPorId(solicitacao.contribuinteId())
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));
        contribuinteRepository.salvar(new Contribuinte(
            contribuinte.id(),
            contribuinte.tenantId(),
            contribuinte.pessoaId(),
            contribuinte.inscricaoMunicipal(),
            contribuinte.tipoContribuinteId(),
            contribuinte.situacaoCadastralId(),
            statusDestinoId,
            contribuinte.regimeTributarioId(),
            contribuinte.nomeContador(),
            contribuinte.emailContador()
        ));

        auditoriaPort.registrar(new RegistroAuditoria(
            "iss_solicitacao_credenciamento",
            solicitacaoId.toString(),
            acaoAuditoria,
            dadosAntes,
            Map.of(
                "statusId", statusDestinoId,
                "observacao", observacao != null ? observacao : "",
                "analisadoPor", analisadorId,
                "analisadoEm", analisadoEm.toString()
            )
        ));

        return solicitacaoRepository.buscarPorId(solicitacaoId).orElseThrow();
    }

    private static UUID usuarioAutenticadoId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ValidationException("Usuário autenticado não identificado.");
        }
        return UUID.fromString(authentication.getName());
    }
}
