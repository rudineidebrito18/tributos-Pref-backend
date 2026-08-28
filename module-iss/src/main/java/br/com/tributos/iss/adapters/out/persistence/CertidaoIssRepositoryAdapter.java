package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.CertidaoIss;
import br.com.tributos.iss.domain.CertidaoIssRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class CertidaoIssRepositoryAdapter implements CertidaoIssRepository {

    private final CertidaoIssJpaRepository jpaRepository;

    public CertidaoIssRepositoryAdapter(CertidaoIssJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
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

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<CertidaoIss> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(CertidaoIssRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<CertidaoIss> buscarPorCodigoVerificacao(String codigoVerificacao) {
        return jpaRepository.findByCodigoVerificacao(codigoVerificacao).map(CertidaoIssRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<CertidaoIss> listar(UUID contribuinteId, Pageable pageable) {
        return jpaRepository.buscarComFiltro(contribuinteId, pageable).map(CertidaoIssRepositoryAdapter::paraDominio);
    }

    @Override
    public long proximoNumero() {
        return jpaRepository.findMaxNumero() + 1;
    }

    private static CertidaoIss paraDominio(CertidaoIssJpaEntity entidade) {
        return new CertidaoIss(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getTipo(),
            entidade.getContribuinteId(),
            entidade.getNumero(),
            entidade.getCodigoVerificacao(),
            entidade.getDataEmissao(),
            entidade.getValidade()
        );
    }
}
