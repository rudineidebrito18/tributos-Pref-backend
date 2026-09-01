package br.com.tributos.iptu.adapters.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.HabiteseImovel;
import br.com.tributos.iptu.domain.HabiteseImovelRepository;
import br.com.tributos.iptu.domain.HabiteseResponsavel;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class HabiteseImovelRepositoryAdapter implements HabiteseImovelRepository {

    private final HabiteseImovelJpaRepository jpaRepository;
    private final HabiteseResponsavelJpaRepository responsavelJpaRepository;

    public HabiteseImovelRepositoryAdapter(
        HabiteseImovelJpaRepository jpaRepository,
        HabiteseResponsavelJpaRepository responsavelJpaRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.responsavelJpaRepository = responsavelJpaRepository;
    }

    @Override
    public HabiteseImovel salvar(HabiteseImovel habitese) {
        UUID tenantId = TenantContext.getObrigatorio();
        HabiteseImovelJpaEntity entidade = jpaRepository.findById(habitese.id())
            .orElseGet(() -> {
                HabiteseImovelJpaEntity nova = new HabiteseImovelJpaEntity();
                nova.setId(habitese.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setImovelId(habitese.imovelId());
        entidade.setTipoId(habitese.tipoId());
        entidade.setNumero(habitese.numero());
        entidade.setDataEmissao(habitese.dataEmissao());
        entidade.setAno(habitese.ano());
        entidade.setValidade(habitese.validade());
        entidade.setContribuinteId(habitese.contribuinteId());
        entidade.setAreaImovel(habitese.areaImovel());
        entidade.setDataConclusao(habitese.dataConclusao());
        entidade.setNumeroAlvara(habitese.numeroAlvara());
        entidade.setDataAlvara(habitese.dataAlvara());
        entidade.setValidadeAlvara(habitese.validadeAlvara());
        entidade.setValorBaseCalculo(habitese.valorBaseCalculo());
        entidade.setBaseCalculo(habitese.baseCalculo());
        entidade.setDesconto(habitese.desconto());
        entidade.setValor(habitese.valor());
        entidade.setFrente(habitese.frente());
        entidade.setFundos(habitese.fundos());
        entidade.setLadoEsquerdo(habitese.ladoEsquerdo());
        entidade.setLadoDireito(habitese.ladoDireito());
        entidade.setObservacao(habitese.observacao());
        entidade.setCodigoVerificacao(habitese.codigoVerificacao());
        entidade.setSituacaoFiscal(habitese.situacaoFiscal());

        HabiteseImovelJpaEntity salva = jpaRepository.save(entidade);
        salvarResponsaveis(tenantId, salva.getId(), habitese.responsaveis());

        return paraDominio(salva, habitese.dataEmissaoTs());
    }

    @Override
    public Optional<HabiteseImovel> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public Page<HabiteseImovel> listarPorImovel(UUID imovelId, Pageable pageable) {
        return jpaRepository.findByImovelIdOrderByNumeroDesc(imovelId, pageable)
            .map(this::paraDominio);
    }

    @Override
    public long proximoNumero() {
        return jpaRepository.findMaxNumero() + 1;
    }

    @Override
    public boolean existeCodigoVerificacao(String codigoVerificacao) {
        return jpaRepository.existsByCodigoVerificacao(codigoVerificacao);
    }

    private void salvarResponsaveis(UUID tenantId, UUID habiteseId, List<HabiteseResponsavel> responsaveis) {
        responsavelJpaRepository.deleteByHabiteseId(habiteseId);
        if (responsaveis == null || responsaveis.isEmpty()) {
            return;
        }
        for (HabiteseResponsavel responsavel : responsaveis) {
            HabiteseResponsavelJpaEntity entidade = new HabiteseResponsavelJpaEntity();
            entidade.setId(responsavel.id() != null ? responsavel.id() : UUID.randomUUID());
            entidade.setTenantId(tenantId);
            entidade.setHabiteseId(habiteseId);
            entidade.setOrdem(responsavel.ordem());
            entidade.setNome(responsavel.nome());
            entidade.setProfissao(responsavel.profissao());
            entidade.setDocumento(responsavel.documento());
            responsavelJpaRepository.save(entidade);
        }
    }

    private HabiteseImovel paraDominio(HabiteseImovelJpaEntity entidade) {
        return paraDominio(entidade, entidade.getDataEmissaoTs());
    }

    private HabiteseImovel paraDominio(HabiteseImovelJpaEntity entidade, Instant fallbackTs) {
        Instant ts = entidade.getDataEmissaoTs() != null ? entidade.getDataEmissaoTs() : fallbackTs;
        return new HabiteseImovel(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getImovelId(),
            entidade.getTipoId(),
            entidade.getNumero(),
            entidade.getDataEmissao(),
            ts,
            entidade.getAno(),
            entidade.getValidade(),
            entidade.getContribuinteId(),
            entidade.getAreaImovel(),
            entidade.getDataConclusao(),
            entidade.getNumeroAlvara(),
            entidade.getDataAlvara(),
            entidade.getValidadeAlvara(),
            entidade.getValorBaseCalculo(),
            entidade.getBaseCalculo(),
            entidade.getDesconto(),
            entidade.getValor(),
            entidade.getFrente(),
            entidade.getFundos(),
            entidade.getLadoEsquerdo(),
            entidade.getLadoDireito(),
            entidade.getObservacao(),
            entidade.getCodigoVerificacao(),
            entidade.getSituacaoFiscal(),
            carregarResponsaveis(entidade.getId())
        );
    }

    private List<HabiteseResponsavel> carregarResponsaveis(UUID habiteseId) {
        return responsavelJpaRepository.findByHabiteseIdOrderByOrdemAsc(habiteseId).stream()
            .map(entidade -> new HabiteseResponsavel(
                entidade.getId(),
                entidade.getOrdem(),
                entidade.getNome(),
                entidade.getProfissao(),
                entidade.getDocumento()
            ))
            .toList();
    }
}
