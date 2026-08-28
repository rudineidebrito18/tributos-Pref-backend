package br.com.tributos.iss.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.SolicitacaoCredenciamento;
import br.com.tributos.iss.domain.SolicitacaoCredenciamentoRepository;

@Service
public class ListarSolicitacoesCredenciamentoService {

    private final SolicitacaoCredenciamentoRepository solicitacaoRepository;

    public ListarSolicitacoesCredenciamentoService(SolicitacaoCredenciamentoRepository solicitacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoCredenciamento> executar(Pageable pageable) {
        return solicitacaoRepository.listar(pageable);
    }
}
