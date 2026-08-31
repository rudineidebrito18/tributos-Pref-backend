package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Contribuinte;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.PessoaReferenciaRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.identity.UsuarioAcessoPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerarSenhaAcessoContribuinteService {

    private final ContribuinteRepository contribuinteRepository;
    private final PessoaReferenciaRepository pessoaReferenciaRepository;
    private final UsuarioAcessoPort usuarioAcessoPort;

    public GerarSenhaAcessoContribuinteService(
        ContribuinteRepository contribuinteRepository,
        PessoaReferenciaRepository pessoaReferenciaRepository,
        UsuarioAcessoPort usuarioAcessoPort
    ) {
        this.contribuinteRepository = contribuinteRepository;
        this.pessoaReferenciaRepository = pessoaReferenciaRepository;
        this.usuarioAcessoPort = usuarioAcessoPort;
    }

    @Transactional
    public Resultado executar(UUID contribuinteId) {
        UUID tenantId = TenantContext.getObrigatorio();
        Contribuinte contribuinte = contribuinteRepository.buscarPorId(contribuinteId)
            .filter(c -> c.tenantId().equals(tenantId))
            .orElseThrow(() -> new NotFoundException("Contribuinte não encontrado."));

        if (contribuinte.usuarioId() != null) {
            throw new ValidationException("Contribuinte já possui usuário de acesso vinculado.");
        }

        var pessoa = pessoaReferenciaRepository.buscarDados(contribuinte.pessoaId())
            .orElseThrow(() -> new ValidationException("Pessoa vinculada não encontrada."));

        if (pessoa.email() == null || pessoa.email().isBlank()) {
            throw new ValidationException("A pessoa vinculada precisa ter e-mail para receber a senha.");
        }

        String login = contribuinte.inscricaoMunicipal();
        var criado = usuarioAcessoPort.criarComSenhaGerada(tenantId, login, pessoa.email(), pessoa.nome());

        Contribuinte atualizado = new Contribuinte(
            contribuinte.id(),
            contribuinte.tenantId(),
            contribuinte.pessoaId(),
            contribuinte.inscricaoMunicipal(),
            contribuinte.tipoContribuinteId(),
            contribuinte.situacaoCadastralId(),
            contribuinte.statusCredenciamentoId(),
            contribuinte.regimeTributarioId(),
            contribuinte.nomeFantasia(),
            contribuinte.inscricaoEstadual(),
            contribuinte.contato(),
            contribuinte.telefone2(),
            contribuinte.emailNota(),
            criado.usuarioId(),
            contribuinte.nomeContador(),
            contribuinte.emailContador()
        );
        contribuinteRepository.salvar(atualizado);

        return new Resultado(criado.login(), criado.emailNotificacao());
    }

    public record Resultado(String loginCriado, String senhaEnviadaPara) {
    }
}
