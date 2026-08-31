package br.com.tributos.iss.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.CatalogoIssRepository;
import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.PessoaDadosResumo;
import br.com.tributos.iss.domain.PessoaReferenciaRepository;
import br.com.tributos.iss.domain.TipoCatalogoIss;

@Service
public class ListarContribuintesService {

    private final ContribuinteRepository contribuinteRepository;
    private final PessoaReferenciaRepository pessoaReferenciaRepository;
    private final CatalogoIssRepository catalogoIssRepository;

    public ListarContribuintesService(
        ContribuinteRepository contribuinteRepository,
        PessoaReferenciaRepository pessoaReferenciaRepository,
        CatalogoIssRepository catalogoIssRepository
    ) {
        this.contribuinteRepository = contribuinteRepository;
        this.pessoaReferenciaRepository = pessoaReferenciaRepository;
        this.catalogoIssRepository = catalogoIssRepository;
    }

    @Transactional(readOnly = true)
    public Page<ContribuinteListagemItem> executar(String busca, Pageable pageable) {
        return contribuinteRepository.listar(busca, pageable).map(this::paraItem);
    }

    private ContribuinteListagemItem paraItem(Contribuinte contribuinte) {
        PessoaDadosResumo pessoa = pessoaReferenciaRepository.buscarDados(contribuinte.pessoaId())
            .orElse(new PessoaDadosResumo("", "", ""));

        String email = contribuinte.emailNota() != null && !contribuinte.emailNota().isBlank()
            ? contribuinte.emailNota()
            : pessoa.email();

        String status = catalogoIssRepository
            .buscarPorId(TipoCatalogoIss.STATUS_CREDENCIAMENTO, contribuinte.statusCredenciamentoId())
            .map(c -> c.nome())
            .orElse("");

        String situacao = catalogoIssRepository
            .buscarPorId(TipoCatalogoIss.SITUACAO_CADASTRAL, contribuinte.situacaoCadastralId())
            .map(c -> c.nome())
            .orElse("");

        return new ContribuinteListagemItem(
            contribuinte.id(),
            pessoa.cpfCnpj(),
            pessoa.nome(),
            email,
            status,
            situacao
        );
    }
}
