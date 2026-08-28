package br.com.tributos.cadastro.adapters.in.web;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.cadastro.adapters.in.web.dto.PessoaResponse;
import br.com.tributos.cadastro.adapters.in.web.dto.SalvarPessoaRequest;
import br.com.tributos.cadastro.application.BuscarPessoaService;
import br.com.tributos.cadastro.application.ListarPessoasService;
import br.com.tributos.cadastro.application.SalvarPessoaComando;
import br.com.tributos.cadastro.application.SalvarPessoaService;
import br.com.tributos.cadastro.domain.Pessoa;

@RestController
@RequestMapping("/api/cadastro/pessoas")
public class PessoaController {

    private final ListarPessoasService listarPessoasService;
    private final BuscarPessoaService buscarPessoaService;
    private final SalvarPessoaService salvarPessoaService;

    public PessoaController(
        ListarPessoasService listarPessoasService,
        BuscarPessoaService buscarPessoaService,
        SalvarPessoaService salvarPessoaService
    ) {
        this.listarPessoasService = listarPessoasService;
        this.buscarPessoaService = buscarPessoaService;
        this.salvarPessoaService = salvarPessoaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<PessoaResumoResponse> listar(
        @RequestParam(required = false) String busca,
        Pageable pageable
    ) {
        return listarPessoasService.executar(busca, pageable).map(PessoaResumoResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public PessoaResponse buscar(@PathVariable UUID id) {
        return PessoaResponse.de(buscarPessoaService.executar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<PessoaResponse> criar(@Valid @RequestBody SalvarPessoaRequest request) {
        Pessoa pessoa = salvarPessoaService.executar(paraComando(request), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(PessoaResponse.de(pessoa));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PutMapping("/{id}")
    public PessoaResponse atualizar(@PathVariable UUID id, @Valid @RequestBody SalvarPessoaRequest request) {
        return PessoaResponse.de(salvarPessoaService.executar(paraComando(request), id));
    }

    private static SalvarPessoaComando paraComando(SalvarPessoaRequest request) {
        return new SalvarPessoaComando(
            request.tipoPessoa(),
            request.cpfCnpj(),
            request.nome(),
            request.nomeFantasia(),
            request.razaoSocial(),
            request.dataNascimentoFundacao(),
            request.email(),
            request.telefone1(),
            request.telefone2(),
            request.ativo(),
            request.enderecos() == null ? java.util.List.of() : request.enderecos().stream()
                .map(e -> new SalvarPessoaComando.EnderecoComando(
                    e.cep(), e.logradouro(), e.numero(), e.complemento(),
                    e.bairro(), e.cidadeId(), e.principal()
                ))
                .toList()
        );
    }

    public record PessoaResumoResponse(UUID id, String tipoPessoa, String cpfCnpj, String nome, boolean ativo) {
        static PessoaResumoResponse de(Pessoa pessoa) {
            return new PessoaResumoResponse(
                pessoa.getId(),
                pessoa.getTipoPessoa().name(),
                pessoa.getCpfCnpj().apenasDigitos(),
                pessoa.getNome(),
                pessoa.isAtivo()
            );
        }
    }
}
