package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.ValorAlvara;
import br.com.tributos.iss.domain.ValorAlvaraRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ValorAlvaraRepositoryAdapter implements ValorAlvaraRepository {

    private final ValorAlvaraJpaRepository jpaRepository;

    public ValorAlvaraRepositoryAdapter(ValorAlvaraJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ValorAlvara salvar(ValorAlvara valorAlvara) {
        UUID tenantId = TenantContext.getObrigatorio();
        ValorAlvaraJpaEntity entidade = jpaRepository.findById(valorAlvara.id())
            .orElseGet(() -> {
                ValorAlvaraJpaEntity nova = new ValorAlvaraJpaEntity();
                nova.setId(valorAlvara.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setTipoAlvaraId(valorAlvara.tipoAlvaraId());
        entidade.setAnoVigencia(valorAlvara.anoVigencia());
        entidade.setValor(valorAlvara.valor());
        entidade.setUsuarioId(valorAlvara.usuarioId());
        entidade.setAtualizadoEm(valorAlvara.atualizadoEm());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public List<ValorAlvara> listarPorTipoAlvara(UUID tipoAlvaraId) {
        return jpaRepository.findByTipoAlvaraIdOrderByAtualizadoEmDesc(tipoAlvaraId).stream()
            .map(ValorAlvaraRepositoryAdapter::paraDominio)
            .toList();
    }

    private static ValorAlvara paraDominio(ValorAlvaraJpaEntity entidade) {
        return new ValorAlvara(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getTipoAlvaraId(),
            entidade.getAnoVigencia(),
            entidade.getValor(),
            entidade.getUsuarioId(),
            entidade.getAtualizadoEm()
        );
    }
}
