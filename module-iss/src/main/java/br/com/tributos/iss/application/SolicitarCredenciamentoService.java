package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.CatalogoIssRepository;
import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.SolicitacaoCredenciamento;
import br.com.tributos.iss.domain.SolicitacaoCredenciamentoRepository;
import br.com.tributos.iss.domain.StatusCredenciamentoNomes;
import br.com.tributos.iss.domain.TipoCatalogoIss;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class SolicitarCredenciamentoService {

    private final ContribuinteRepository contribuinteRepository;
    private final SolicitacaoCredenciamentoRepository solicitacaoRepository;
    private final CatalogoIssRepository catalogoIssRepository;

    public SolicitarCredenciamentoService(
        ContribuinteRepository contribuinteRepository,
        SolicitacaoCredenciamentoRepository solicitacaoRepository,
        CatalogoIssRepository catalogoIssRepository
    ) {
        this.contribuinteRepository = contribuinteRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.catalogoIssRepository = catalogoIssRepository;
    }

    @Transactional
    public SolicitacaoCredenciamento executar(UUID contribuinteId) {
        Contribuinte contribuinte = contribuinteRepository.buscarPorId(contribuinteId)
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));

        UUID statusEmAnaliseId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.EM_ANALISE)
            .orElseThrow(() -> new IllegalStateException("Status EM_ANALISE não encontrado no catálogo do tenant."))
            .id();

        validarPodeSolicitar(contribuinte);

        if (solicitacaoRepository.buscarEmAnalisePorContribuinte(contribuinteId).isPresent()) {
            throw new ValidationException("Já existe uma solicitação de credenciamento em análise para este contribuinte.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        SolicitacaoCredenciamento solicitacao = new SolicitacaoCredenciamento(
            UUID.randomUUID(),
            tenantId,
            contribuinteId,
            statusEmAnaliseId,
            null,
            null,
            null,
            null
        );
        solicitacaoRepository.salvar(solicitacao);

        Contribuinte atualizado = new Contribuinte(
            contribuinte.id(),
            contribuinte.tenantId(),
            contribuinte.pessoaId(),
            contribuinte.inscricaoMunicipal(),
            contribuinte.tipoContribuinteId(),
            contribuinte.situacaoCadastralId(),
            statusEmAnaliseId,
            contribuinte.regimeTributarioId(),
            contribuinte.nomeContador(),
            contribuinte.emailContador()
        );
        contribuinteRepository.salvar(atualizado);

        return solicitacaoRepository.buscarPorId(solicitacao.id()).orElseThrow();
    }

    private void validarPodeSolicitar(Contribuinte contribuinte) {
        UUID naoCredenciadoId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.NAO_CREDENCIADO)
            .map(c -> c.id())
            .orElseThrow();
        UUID negadoId = catalogoIssRepository
            .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.NEGADO)
            .map(c -> c.id())
            .orElseThrow();

        UUID statusAtual = contribuinte.statusCredenciamentoId();
        if (!statusAtual.equals(naoCredenciadoId) && !statusAtual.equals(negadoId)) {
            throw new ValidationException("O contribuinte não está elegível para nova solicitação de credenciamento.");
        }
    }
}
