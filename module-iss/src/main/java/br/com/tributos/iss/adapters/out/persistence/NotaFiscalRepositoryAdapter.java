package br.com.tributos.iss.adapters.out.persistence;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.NotaFiscal;
import br.com.tributos.iss.domain.NotaFiscalRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class NotaFiscalRepositoryAdapter implements NotaFiscalRepository {

    private final NotaFiscalJpaRepository jpaRepository;

    public NotaFiscalRepositoryAdapter(NotaFiscalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public NotaFiscal salvar(NotaFiscal notaFiscal) {
        UUID tenantId = TenantContext.getObrigatorio();
        NotaFiscalJpaEntity entidade = jpaRepository.findById(notaFiscal.id())
            .orElseGet(() -> {
                NotaFiscalJpaEntity nova = new NotaFiscalJpaEntity();
                nova.setId(notaFiscal.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setNumero(notaFiscal.numero());
        entidade.setSerie(notaFiscal.serie());
        entidade.setContribuinteId(notaFiscal.contribuinteId());
        entidade.setTomadorId(notaFiscal.tomadorId());
        entidade.setServicoId(notaFiscal.servicoId());
        entidade.setCompetencia(notaFiscal.competencia());
        entidade.setValorServico(notaFiscal.valorServico());
        entidade.setValorDeducoes(notaFiscal.valorDeducoes());
        entidade.setBaseCalculo(notaFiscal.baseCalculo());
        entidade.setAliquotaAplicada(notaFiscal.aliquotaAplicada());
        entidade.setValorIss(notaFiscal.valorIss());
        entidade.setStatus(notaFiscal.status());
        entidade.setNotaSubstitutaId(notaFiscal.notaSubstitutaId());
        entidade.setMotivoCancelamento(notaFiscal.motivoCancelamento());
        entidade.setDataEmissao(notaFiscal.dataEmissao());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<NotaFiscal> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(NotaFiscalRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<NotaFiscal> listar(UUID contribuinteId, UUID tomadorId, LocalDate competencia, Pageable pageable) {
        return jpaRepository.buscarComFiltro(contribuinteId, tomadorId, competencia, pageable)
            .map(NotaFiscalRepositoryAdapter::paraDominio);
    }

    @Override
    public long proximoNumero() {
        return jpaRepository.findMaxNumero() + 1;
    }

    private static NotaFiscal paraDominio(NotaFiscalJpaEntity entidade) {
        return new NotaFiscal(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getNumero(),
            entidade.getSerie(),
            entidade.getContribuinteId(),
            entidade.getTomadorId(),
            entidade.getServicoId(),
            entidade.getCompetencia(),
            entidade.getValorServico(),
            entidade.getValorDeducoes(),
            entidade.getBaseCalculo(),
            entidade.getAliquotaAplicada(),
            entidade.getValorIss(),
            entidade.getStatus(),
            entidade.getNotaSubstitutaId(),
            entidade.getMotivoCancelamento(),
            entidade.getDataEmissao()
        );
    }
}
