package br.com.tributos.iptu.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.CertidaoNegativaImovel;
import br.com.tributos.iptu.domain.CertidaoNegativaImovelRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class CertidaoNegativaImovelRepositoryAdapter implements CertidaoNegativaImovelRepository {

    private final CertidaoNegativaImovelJpaRepository jpaRepository;

    public CertidaoNegativaImovelRepositoryAdapter(CertidaoNegativaImovelJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CertidaoNegativaImovel salvar(CertidaoNegativaImovel certidao) {
        UUID tenantId = TenantContext.getObrigatorio();
        CertidaoNegativaImovelJpaEntity entidade = jpaRepository.findById(certidao.id())
            .orElseGet(() -> {
                CertidaoNegativaImovelJpaEntity nova = new CertidaoNegativaImovelJpaEntity();
                nova.setId(certidao.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setImovelId(certidao.imovelId());
        entidade.setNumero(certidao.numero());
        entidade.setDataEmissao(certidao.dataEmissao());
        entidade.setValidade(certidao.validade());
        entidade.setCodigoVerificacao(certidao.codigoVerificacao());
        entidade.setSituacaoCndId(certidao.situacaoCndId());
        entidade.setObservacao(certidao.observacao());

        CertidaoNegativaImovelJpaEntity salva = jpaRepository.save(entidade);
        return paraDominio(salva, certidao.dataEmissaoTs());
    }

    @Override
    public Optional<CertidaoNegativaImovel> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(CertidaoNegativaImovelRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<CertidaoNegativaImovel> listarPorImovel(UUID imovelId, Pageable pageable) {
        return jpaRepository.findByImovelIdOrderByNumeroDesc(imovelId, pageable)
            .map(CertidaoNegativaImovelRepositoryAdapter::paraDominio);
    }

    @Override
    public long proximoNumero() {
        return jpaRepository.findMaxNumero() + 1;
    }

    private static CertidaoNegativaImovel paraDominio(CertidaoNegativaImovelJpaEntity entidade, java.time.Instant fallbackTs) {
        java.time.Instant ts = entidade.getDataEmissaoTs() != null ? entidade.getDataEmissaoTs() : fallbackTs;
        return new CertidaoNegativaImovel(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getImovelId(),
            entidade.getNumero(),
            entidade.getDataEmissao(),
            entidade.getValidade(),
            entidade.getCodigoVerificacao(),
            ts,
            entidade.getSituacaoCndId(),
            entidade.getObservacao()
        );
    }

    private static CertidaoNegativaImovel paraDominio(CertidaoNegativaImovelJpaEntity entidade) {
        return paraDominio(entidade, null);
    }
}
