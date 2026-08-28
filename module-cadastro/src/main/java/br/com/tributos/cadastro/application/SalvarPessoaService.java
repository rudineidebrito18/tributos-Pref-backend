package br.com.tributos.cadastro.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.Endereco;
import br.com.tributos.cadastro.domain.Pessoa;
import br.com.tributos.cadastro.domain.PessoaRepository;
import br.com.tributos.cadastro.domain.TipoPessoa;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;
import br.com.tributos.kernel.vo.CpfCnpj;

@Service
public class SalvarPessoaService {

    private final PessoaRepository pessoaRepository;

    public SalvarPessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional
    public Pessoa executar(SalvarPessoaComando comando, UUID idExistente) {
        CpfCnpj documento = CpfCnpj.de(comando.cpfCnpj());
        TipoPessoa tipo = TipoPessoa.valueOf(comando.tipoPessoa().toUpperCase());

        if (tipo == TipoPessoa.PF && documento.tipo() != CpfCnpj.Tipo.CPF) {
            throw new ValidationException("Informe um CPF válido para pessoa física.");
        }
        if (tipo == TipoPessoa.PJ && documento.tipo() != CpfCnpj.Tipo.CNPJ) {
            throw new ValidationException("Informe um CNPJ válido para pessoa jurídica.");
        }

        if (pessoaRepository.existePorCpfCnpj(documento.apenasDigitos(), idExistente)) {
            throw new ValidationException("Já existe uma pessoa cadastrada com este CPF/CNPJ.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        UUID id = idExistente != null ? idExistente : UUID.randomUUID();

        List<Endereco> enderecos = comando.enderecos().stream()
            .map(e -> new Endereco(
                UUID.randomUUID(), tenantId, id,
                e.logradouro(), e.numero(), e.complemento(), e.bairro(),
                e.cidadeId(), normalizarCep(e.cep()), e.principal()
            ))
            .toList();

        if (idExistente == null) {
            Pessoa pessoa = new Pessoa(
                id, tenantId, tipo, documento, comando.nome(), comando.nomeFantasia(),
                comando.razaoSocial(), comando.dataNascimentoFundacao(), comando.email(),
                comando.telefone1(), comando.telefone2(), comando.ativo()
            );
            pessoa.substituirEnderecos(enderecos);
            return pessoaRepository.salvar(pessoa);
        }

        Pessoa existente = pessoaRepository.buscarPorId(idExistente)
            .orElseThrow(() -> new br.com.tributos.kernel.exception.NotFoundException("Pessoa não encontrada."));
        if (!existente.getCpfCnpj().apenasDigitos().equals(documento.apenasDigitos())) {
            throw new ValidationException("O CPF/CNPJ não pode ser alterado após o cadastro.");
        }
        existente.substituirDados(
            comando.nome(), comando.nomeFantasia(), comando.razaoSocial(),
            comando.dataNascimentoFundacao(), comando.email(),
            comando.telefone1(), comando.telefone2(), comando.ativo()
        );
        existente.substituirEnderecos(enderecos);
        return pessoaRepository.salvar(existente);
    }

    private static String normalizarCep(String cep) {
        if (cep == null || cep.isBlank()) return null;
        return CpfCnpj.normalizar(cep);
    }
}
