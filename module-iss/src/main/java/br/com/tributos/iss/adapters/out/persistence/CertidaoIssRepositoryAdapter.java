package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.CertidaoIss;
import br.com.tributos.iss.domain.CertidaoIssRepository;
import br.com.tributos.iss.domain.TributoCertidao;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class CertidaoIssRepositoryAdapter implements CertidaoIssRepository {

    private final CertidaoIssJpaRepository jpaRepository;
    private final CertidaoIssTributoJpaRepository tributoJpaRepository;

    public CertidaoIssRepositoryAdapter(
        CertidaoIssJpaRepository jpaRepository,
        CertidaoIssTributoJpaRepository tributoJpaRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.tributoJpaRepository = tributoJpaRepository;
    }

    @Override
    public CertidaoIss salvar(CertidaoIss certidao) {
        UUID tenantId = TenantContext.getObrigatorio();
        CertidaoIssJpaEntity entidade = jpaRepository.findById(certidao.id())
            .orElseGet(() -> {
                CertidaoIssJpaEntity nova = new CertidaoIssJpaEntity();
                nova.setId(certidao.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setTipo(certidao.tipo());
        entidade.setContribuinteId(certidao.contribuinteId());
        entidade.setNumero(certidao.numero());
        entidade.setCodigoVerificacao(certidao.codigoVerificacao());
        entidade.setDataEmissao(certidao.dataEmissao());
        entidade.setValidade(certidao.validade());
        entidade.setSituacaoCndId(certidao.situacaoCndId());
        entidade.setObservacao(certidao.observacao());
        entidade.setAvulsa(certidao.avulsa());

        jpaRepository.save(entidade);
        salvarTributos(tenantId, certidao.id(), certidao.tributos());

        return buscarPorId(certidao.id()).orElseThrow();
    }

    @Override
    public Optional<CertidaoIss> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public Optional<CertidaoIss> buscarPorCodigoVerificacao(String codigoVerificacao) {
        return jpaRepository.findByCodigoVerificacao(codigoVerificacao).map(this::paraDominio);
    }

    @Override
    public Page<CertidaoIss> listar(UUID contribuinteId, Pageable pageable) {
        return jpaRepository.buscarComFiltro(contribuinteId, pageable).map(this::paraDominio);
    }

    @Override
    public long proximoNumero() {
        return jpaRepository.findMaxNumero() + 1;
    }

    private CertidaoIss paraDominio(CertidaoIssJpaEntity entidade) {
        List<TributoCertidao> tributos = tributoJpaRepository.findByCertidaoId(entidade.getId()).stream()
            .map(CertidaoIssTributoJpaEntity::getTributo)
            .toList();

        return new CertidaoIss(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getTipo(),
            entidade.getContribuinteId(),
            entidade.getNumero(),
            entidade.getCodigoVerificacao(),
            entidade.getDataEmissao(),
            entidade.getValidade(),
            entidade.getSituacaoCndId(),
            entidade.getObservacao(),
            entidade.isAvulsa(),
            tributos
        );
    }

    private void salvarTributos(UUID tenantId, UUID certidaoId, List<TributoCertidao> tributos) {
        tributoJpaRepository.deleteByCertidaoId(certidaoId);
        if (tributos == null || tributos.isEmpty()) {
            return;
        }
        for (TributoCertidao tributo : tributos) {
            CertidaoIssTributoJpaEntity entidade = new CertidaoIssTributoJpaEntity();
            entidade.setId(UUID.randomUUID());
            entidade.setTenantId(tenantId);
            entidade.setCertidaoId(certidaoId);
            entidade.setTributo(tributo);
            tributoJpaRepository.save(entidade);
        }
    }
}
