package br.com.tributos.cadastro.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.tributos.kernel.vo.CpfCnpj;

public class Pessoa {

    private final UUID id;
    private final UUID tenantId;
    private TipoPessoa tipoPessoa;
    private CpfCnpj cpfCnpj;
    private String nome;
    private String nomeFantasia;
    private String razaoSocial;
    private LocalDate dataNascimentoFundacao;
    private String email;
    private String telefone1;
    private String telefone2;
    private boolean ativo;
    private final List<Endereco> enderecos = new ArrayList<>();

    public Pessoa(
        UUID id,
        UUID tenantId,
        TipoPessoa tipoPessoa,
        CpfCnpj cpfCnpj,
        String nome,
        String nomeFantasia,
        String razaoSocial,
        LocalDate dataNascimentoFundacao,
        String email,
        String telefone1,
        String telefone2,
        boolean ativo
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.tipoPessoa = tipoPessoa;
        this.cpfCnpj = cpfCnpj;
        this.nome = nome;
        this.nomeFantasia = nomeFantasia;
        this.razaoSocial = razaoSocial;
        this.dataNascimentoFundacao = dataNascimentoFundacao;
        this.email = email;
        this.telefone1 = telefone1;
        this.telefone2 = telefone2;
        this.ativo = ativo;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public CpfCnpj getCpfCnpj() {
        return cpfCnpj;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public LocalDate getDataNascimentoFundacao() {
        return dataNascimentoFundacao;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone1() {
        return telefone1;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public List<Endereco> getEnderecos() {
        return List.copyOf(enderecos);
    }

    public void substituirDados(
        String nome,
        String nomeFantasia,
        String razaoSocial,
        LocalDate dataNascimentoFundacao,
        String email,
        String telefone1,
        String telefone2,
        boolean ativo
    ) {
        this.nome = nome;
        this.nomeFantasia = nomeFantasia;
        this.razaoSocial = razaoSocial;
        this.dataNascimentoFundacao = dataNascimentoFundacao;
        this.email = email;
        this.telefone1 = telefone1;
        this.telefone2 = telefone2;
        this.ativo = ativo;
    }

    public void substituirEnderecos(List<Endereco> novosEnderecos) {
        enderecos.clear();
        enderecos.addAll(novosEnderecos);
    }
}
