package br.com.tributos.cadastro.adapters.in.web.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.tributos.cadastro.domain.Endereco;
import br.com.tributos.cadastro.domain.Pessoa;

public record PessoaResponse(
    UUID id,
    String tipoPessoa,
    String cpfCnpj,
    String nome,
    String nomeFantasia,
    String razaoSocial,
    LocalDate dataNascimentoFundacao,
    String email,
    String telefone1,
    String telefone2,
    boolean ativo,
    List<EnderecoResponse> enderecos
) {

    public static PessoaResponse de(Pessoa pessoa) {
        return new PessoaResponse(
            pessoa.getId(),
            pessoa.getTipoPessoa().name(),
            pessoa.getCpfCnpj().apenasDigitos(),
            pessoa.getNome(),
            pessoa.getNomeFantasia(),
            pessoa.getRazaoSocial(),
            pessoa.getDataNascimentoFundacao(),
            pessoa.getEmail(),
            pessoa.getTelefone1(),
            pessoa.getTelefone2(),
            pessoa.isAtivo(),
            pessoa.getEnderecos().stream().map(EnderecoResponse::de).toList()
        );
    }

    public record EnderecoResponse(
        UUID id,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        UUID cidadeId,
        boolean principal
    ) {
        static EnderecoResponse de(Endereco endereco) {
            return new EnderecoResponse(
                endereco.id(),
                endereco.cep(),
                endereco.logradouroTexto(),
                endereco.numero(),
                endereco.complemento(),
                endereco.bairroTexto(),
                endereco.cidadeId(),
                endereco.principal()
            );
        }
    }
}
