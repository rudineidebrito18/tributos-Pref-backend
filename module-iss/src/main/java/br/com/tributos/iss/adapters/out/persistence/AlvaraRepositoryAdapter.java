package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.Alvara;
import br.com.tributos.iss.domain.AlvaraRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class AlvaraRepositoryAdapter implements AlvaraRepository {

    private final AlvaraJpaRepository jpaRepository;

    public AlvaraRepositoryAdapter(AlvaraJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Alvara salvar(Alvara alvara) {
        UUID tenantId = TenantContext.getObrigatorio();
        AlvaraJpaEntity entidade = jpaRepository.findById(alvara.id())
            .orElseGet(() -> {
                AlvaraJpaEntity nova = new AlvaraJpaEntity();
                nova.setId(alvara.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setNumero(alvara.numero());
        entidade.setTipoAlvaraId(alvara.tipoAlvaraId());
        entidade.setContribuinteId(alvara.contribuinteId());
        entidade.setDataExpedicao(alvara.dataExpedicao());
        entidade.setValidade(alvara.validade());
        entidade.setSituacaoFiscal(alvara.situacaoFiscal());
        entidade.setValor(alvara.valor());
        entidade.setCodigoVerificacao(alvara.codigoVerificacao());
        entidade.setDataEmissao(alvara.dataEmissao());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Alvara> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(AlvaraRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<Alvara> buscarPorCodigoVerificacao(String codigoVerificacao) {
        return jpaRepository.findByCodigoVerificacao(codigoVerificacao).map(AlvaraRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<Alvara> listar(UUID contribuinteId, Pageable pageable) {
        return jpaRepository.buscarComFiltro(contribuinteId, pageable).map(AlvaraRepositoryAdapter::paraDominio);
    }

    @Override
    public long proximoNumero() {
        return jpaRepository.findMaxNumero() + 1;
    }

    private static Alvara paraDominio(AlvaraJpaEntity entidade) {
        return new Alvara(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getNumero(),
            entidade.getTipoAlvaraId(),
            entidade.getContribuinteId(),
            entidade.getDataExpedicao(),
            entidade.getValidade(),
            entidade.getSituacaoFiscal(),
            entidade.getValor(),
            entidade.getCodigoVerificacao(),
            entidade.getDataEmissao()
        );
    }
}
