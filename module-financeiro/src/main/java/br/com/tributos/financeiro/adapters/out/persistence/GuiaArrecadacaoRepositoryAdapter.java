package br.com.tributos.financeiro.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class GuiaArrecadacaoRepositoryAdapter implements GuiaArrecadacaoRepository {

    private final GuiaArrecadacaoJpaRepository jpaRepository;

    public GuiaArrecadacaoRepositoryAdapter(GuiaArrecadacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public GuiaArrecadacao salvar(GuiaArrecadacao guia) {
        return paraDominio(jpaRepository.save(paraEntidade(guia)));
    }

    @Override
    public Optional<GuiaArrecadacao> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public Optional<GuiaArrecadacao> buscarPorOrigem(OrigemGuia origemTipo, UUID origemId) {
        return jpaRepository.findByOrigemTipoAndOrigemId(origemTipo, origemId).map(this::paraDominio);
    }

    @Override
    public Optional<GuiaArrecadacao> buscarPorNumero(long numero) {
        return jpaRepository.findByNumero(numero).map(this::paraDominio);
    }

    @Override
    public Page<GuiaArrecadacao> listar(
        TipoTributo tipoTributo,
        SituacaoGuia situacao,
        UUID contribuinteId,
        Pageable pageable
    ) {
        return jpaRepository.buscarComFiltro(tipoTributo, situacao, contribuinteId, pageable).map(this::paraDominio);
    }

    @Override
    public long proximoNumero() {
        return jpaRepository.maxNumero() + 1;
    }

    @Override
    public boolean possuiPendencia(UUID tenantId, UUID pessoaId) {
        return jpaRepository.existsByTenantIdAndContribuinteIdAndSituacao(tenantId, pessoaId, SituacaoGuia.PENDENTE);
    }

    private GuiaArrecadacao paraDominio(GuiaArrecadacaoJpaEntity e) {
        return new GuiaArrecadacao(
            e.getId(),
            e.getTenantId(),
            e.getNumero(),
            e.getTipoTributo(),
            e.getOrigemTipo(),
            e.getOrigemId(),
            e.getContribuinteId(),
            e.getCompetenciaMes(),
            e.getCompetenciaAno(),
            e.getDataEmissao(),
            e.getDataVencimento(),
            e.getValor(),
            e.getSituacao(),
            e.getFormaPagamentoId(),
            e.getDataEfetivacao(),
            e.getValorPago(),
            e.getCodigoBarras(),
            e.getPixTxid(),
            e.getDescricaoAvulsa()
        );
    }

    private GuiaArrecadacaoJpaEntity paraEntidade(GuiaArrecadacao g) {
        GuiaArrecadacaoJpaEntity e = new GuiaArrecadacaoJpaEntity();
        e.setId(g.id());
        e.setTenantId(g.tenantId());
        e.setNumero(g.numero());
        e.setTipoTributo(g.tipoTributo());
        e.setOrigemTipo(g.origemTipo());
        e.setOrigemId(g.origemId());
        e.setContribuinteId(g.contribuinteId());
        e.setCompetenciaMes(g.competenciaMes());
        e.setCompetenciaAno(g.competenciaAno());
        e.setDataEmissao(g.dataEmissao());
        e.setDataVencimento(g.dataVencimento());
        e.setValor(g.valor());
        e.setSituacao(g.situacao());
        e.setFormaPagamentoId(g.formaPagamentoId());
        e.setDataEfetivacao(g.dataEfetivacao());
        e.setValorPago(g.valorPago());
        e.setCodigoBarras(g.codigoBarras());
        e.setPixTxid(g.pixTxid());
        e.setDescricaoAvulsa(g.descricaoAvulsa());
        return e;
    }
}
