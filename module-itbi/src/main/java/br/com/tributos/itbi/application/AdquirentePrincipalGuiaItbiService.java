package br.com.tributos.itbi.application;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.itbi.domain.ContribuinteReferenciaRepository;
import br.com.tributos.itbi.domain.PapelParteTransmissao;
import br.com.tributos.itbi.domain.ParteTransmissao;
import br.com.tributos.itbi.domain.ParteTransmissaoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class AdquirentePrincipalGuiaItbiService {

    private final ParteTransmissaoRepository parteTransmissaoRepository;
    private final ContribuinteReferenciaRepository contribuinteReferenciaRepository;

    public AdquirentePrincipalGuiaItbiService(
        ParteTransmissaoRepository parteTransmissaoRepository,
        ContribuinteReferenciaRepository contribuinteReferenciaRepository
    ) {
        this.parteTransmissaoRepository = parteTransmissaoRepository;
        this.contribuinteReferenciaRepository = contribuinteReferenciaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UUID> buscarPessoaIdPrincipal(UUID guiaId) {
        return buscarPrincipal(guiaId)
            .flatMap(p -> contribuinteReferenciaRepository.buscarPessoaId(p.contribuinteId()));
    }

    @Transactional
    public void registrarAdquirenteInicial(UUID guiaId, UUID pessoaId) {
        if (pessoaId == null) {
            return;
        }

        UUID contribuinteId = contribuinteReferenciaRepository.buscarContribuinteIdPorPessoaId(pessoaId)
            .orElseThrow(() -> new br.com.tributos.kernel.exception.ValidationException(
                "Cadastre o adquirente como contribuinte ISS antes de solicitar a guia ITBI."
            ));

        if (!parteTransmissaoRepository.listarPorGuiaEPapel(guiaId, PapelParteTransmissao.ADQUIRENTE).isEmpty()) {
            return;
        }

        UUID tenantId = TenantContext.getObrigatorio();
        parteTransmissaoRepository.salvar(new ParteTransmissao(
            UUID.randomUUID(),
            tenantId,
            guiaId,
            contribuinteId,
            PapelParteTransmissao.ADQUIRENTE,
            new BigDecimal("100"),
            true
        ));
    }

    private Optional<ParteTransmissao> buscarPrincipal(UUID guiaId) {
        return parteTransmissaoRepository.buscarPrincipalPorGuiaEPapel(guiaId, PapelParteTransmissao.ADQUIRENTE)
            .or(() -> parteTransmissaoRepository.listarPorGuiaEPapel(guiaId, PapelParteTransmissao.ADQUIRENTE)
                .stream()
                .findFirst());
    }
}
