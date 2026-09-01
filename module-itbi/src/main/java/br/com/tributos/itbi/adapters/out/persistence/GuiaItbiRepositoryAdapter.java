package br.com.tributos.itbi.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.itbi.domain.GuiaItbi;
import br.com.tributos.itbi.domain.GuiaItbiRepository;

@Component
public class GuiaItbiRepositoryAdapter implements GuiaItbiRepository {

    private final GuiaItbiJpaRepository jpaRepository;

    public GuiaItbiRepositoryAdapter(GuiaItbiJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public GuiaItbi salvar(GuiaItbi guia) {
        return paraDominio(jpaRepository.save(paraEntidade(guia)));
    }

    @Override
    public Optional<GuiaItbi> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public Page<GuiaItbi> listar(UUID imovelId, Pageable pageable) {
        return jpaRepository.buscarComFiltro(imovelId, pageable).map(this::paraDominio);
    }

    @Override
    public long proximoNumero() {
        return jpaRepository.maxNumero() + 1;
    }

    private GuiaItbi paraDominio(GuiaItbiJpaEntity e) {
        return new GuiaItbi(
            e.getId(), e.getTenantId(), e.getNumero(), e.getImovelId(),
            e.getTipoGuiaId(), e.getNaturezaTransmissaoId(), e.getDataSolicitacao(),
            e.getValorTransacao(), e.getValorVenalReferencia(), e.getBaseCalculo(), e.getAliquota(),
            e.getValorItbi(), e.getSituacao(), e.isTransferenciaTitularidadeRealizada(),
            e.getDataTransacao(), e.getPercentualTransmitido(), e.getValorNaoFinanciado(),
            e.getValorFinanciado(), e.getDesconto(), e.getTipoTributacao(), e.getObservacao(),
            e.getMotivoCancelamento(), e.getCodigoVerificacao()
        );
    }

    private GuiaItbiJpaEntity paraEntidade(GuiaItbi g) {
        GuiaItbiJpaEntity e = new GuiaItbiJpaEntity();
        e.setId(g.id());
        e.setTenantId(g.tenantId());
        e.setNumero(g.numero());
        e.setImovelId(g.imovelId());
        e.setTipoGuiaId(g.tipoGuiaId());
        e.setNaturezaTransmissaoId(g.naturezaTransmissaoId());
        e.setDataSolicitacao(g.dataSolicitacao());
        e.setValorTransacao(g.valorTransacao());
        e.setValorVenalReferencia(g.valorVenalReferencia());
        e.setBaseCalculo(g.baseCalculo());
        e.setAliquota(g.aliquota());
        e.setValorItbi(g.valorItbi());
        e.setSituacao(g.situacao());
        e.setTransferenciaTitularidadeRealizada(g.transferenciaTitularidadeRealizada());
        e.setDataTransacao(g.dataTransacao());
        e.setPercentualTransmitido(g.percentualTransmitido());
        e.setValorNaoFinanciado(g.valorNaoFinanciado());
        e.setValorFinanciado(g.valorFinanciado());
        e.setDesconto(g.desconto());
        e.setTipoTributacao(g.tipoTributacao());
        e.setObservacao(g.observacao());
        e.setMotivoCancelamento(g.motivoCancelamento());
        e.setCodigoVerificacao(g.codigoVerificacao());
        return e;
    }
}
