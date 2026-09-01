package br.com.tributos.iptu.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.adapters.out.persistence.IptuContribuinteReferenciaJpaRepository;
import br.com.tributos.iptu.adapters.out.persistence.IptuPessoaReferenciaJpaRepository;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.ImovelTitularidadeHistorico;
import br.com.tributos.iptu.domain.ImovelTitularidadeHistoricoRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class ListarImovelTitularidadeService {

    private final ImovelRepository imovelRepository;
    private final ImovelTitularidadeHistoricoRepository titularidadeHistoricoRepository;
    private final IptuContribuinteReferenciaJpaRepository contribuinteReferenciaJpaRepository;
    private final IptuPessoaReferenciaJpaRepository pessoaReferenciaJpaRepository;

    public ListarImovelTitularidadeService(
        ImovelRepository imovelRepository,
        ImovelTitularidadeHistoricoRepository titularidadeHistoricoRepository,
        IptuContribuinteReferenciaJpaRepository contribuinteReferenciaJpaRepository,
        IptuPessoaReferenciaJpaRepository pessoaReferenciaJpaRepository
    ) {
        this.imovelRepository = imovelRepository;
        this.titularidadeHistoricoRepository = titularidadeHistoricoRepository;
        this.contribuinteReferenciaJpaRepository = contribuinteReferenciaJpaRepository;
        this.pessoaReferenciaJpaRepository = pessoaReferenciaJpaRepository;
    }

    @Transactional(readOnly = true)
    public List<ImovelTitularidadeComContribuinte> executar(UUID imovelId) {
        if (imovelRepository.buscarPorId(imovelId).isEmpty()) {
            throw new NotFoundException("Imóvel não encontrado.");
        }

        return titularidadeHistoricoRepository.listarPorImovel(imovelId).stream()
            .map(this::comContribuinte)
            .toList();
    }

    private ImovelTitularidadeComContribuinte comContribuinte(ImovelTitularidadeHistorico historico) {
        String nome = contribuinteReferenciaJpaRepository.findById(historico.contribuinteId())
            .flatMap(c -> pessoaReferenciaJpaRepository.findById(c.getPessoaId()))
            .map(p -> p.getNome())
            .orElse("—");
        return new ImovelTitularidadeComContribuinte(historico, nome);
    }
}
