package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.CatalogoIssRepository;
import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.PessoaReferenciaRepository;
import br.com.tributos.iss.domain.StatusCredenciamentoNomes;
import br.com.tributos.iss.domain.TipoCatalogoIss;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class SalvarContribuinteService {

    private final ContribuinteRepository contribuinteRepository;
    private final PessoaReferenciaRepository pessoaReferenciaRepository;
    private final CatalogoIssRepository catalogoIssRepository;

    public SalvarContribuinteService(
        ContribuinteRepository contribuinteRepository,
        PessoaReferenciaRepository pessoaReferenciaRepository,
        CatalogoIssRepository catalogoIssRepository
    ) {
        this.contribuinteRepository = contribuinteRepository;
        this.pessoaReferenciaRepository = pessoaReferenciaRepository;
        this.catalogoIssRepository = catalogoIssRepository;
    }

    @Transactional
    public Contribuinte executar(SalvarContribuinteComando comando, UUID idExistente) {
        if (!pessoaReferenciaRepository.existe(comando.pessoaId())) {
            throw new ValidationException("Pessoa não encontrada para vincular ao contribuinte.");
        }

        String inscricao = comando.inscricaoMunicipal().trim();
        if (contribuinteRepository.existePorInscricaoMunicipal(inscricao, idExistente)) {
            throw new ValidationException("Já existe um contribuinte com esta inscrição municipal.");
        }
        if (contribuinteRepository.existePorPessoaId(comando.pessoaId(), idExistente)) {
            throw new ValidationException("Esta pessoa já possui cadastro de contribuinte ISS.");
        }

        validarCatalogo(TipoCatalogoIss.TIPO_CONTRIBUINTE, comando.tipoContribuinteId(), "tipo de contribuinte");
        validarCatalogo(TipoCatalogoIss.SITUACAO_CADASTRAL, comando.situacaoCadastralId(), "situação cadastral");
        validarCatalogo(TipoCatalogoIss.REGIME_TRIBUTARIO, comando.regimeTributarioId(), "regime tributário");

        UUID tenantId = TenantContext.getObrigatorio();
        UUID id = idExistente != null ? idExistente : UUID.randomUUID();

        UUID statusCredenciamentoId;
        if (idExistente == null) {
            statusCredenciamentoId = catalogoIssRepository
                .buscarPorNome(TipoCatalogoIss.STATUS_CREDENCIAMENTO, StatusCredenciamentoNomes.NAO_CREDENCIADO)
                .orElseThrow(() -> new IllegalStateException(
                    "Status NAO_CREDENCIADO não encontrado no catálogo do tenant."))
                .id();
        } else {
            Contribuinte existente = contribuinteRepository.buscarPorId(idExistente)
                .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));
            if (!existente.pessoaId().equals(comando.pessoaId())) {
                throw new ValidationException("A pessoa vinculada não pode ser alterada após o cadastro.");
            }
            statusCredenciamentoId = existente.statusCredenciamentoId();
        }

        UUID usuarioId = comando.usuarioId();
        if (idExistente != null && usuarioId == null) {
            usuarioId = contribuinteRepository.buscarPorId(idExistente)
                .map(Contribuinte::usuarioId)
                .orElse(null);
        }

        if (comando.emailNota() != null && !comando.emailNota().isBlank()
            && !comando.emailNota().matches(".+@.+\\..+")) {
            throw new ValidationException("E-mail para recebimento de notas inválido.");
        }

        Contribuinte contribuinte = new Contribuinte(
            id,
            tenantId,
            comando.pessoaId(),
            inscricao,
            comando.tipoContribuinteId(),
            comando.situacaoCadastralId(),
            statusCredenciamentoId,
            comando.regimeTributarioId(),
            normalizar(comando.nomeFantasia()),
            normalizar(comando.inscricaoEstadual()),
            normalizar(comando.contato()),
            normalizar(comando.telefone2()),
            normalizar(comando.emailNota()),
            usuarioId,
            comando.nomeContador(),
            comando.emailContador()
        );
        return contribuinteRepository.salvar(contribuinte);
    }

    private static String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String trim = valor.trim();
        return trim.isEmpty() ? null : trim;
    }

    private void validarCatalogo(TipoCatalogoIss tipo, UUID id, String rotulo) {
        if (catalogoIssRepository.buscarPorId(tipo, id).isEmpty()) {
            throw new ValidationException("Informe um(a) " + rotulo + " válido(a).");
        }
    }
}
