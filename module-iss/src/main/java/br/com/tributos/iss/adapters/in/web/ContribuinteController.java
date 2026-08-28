package br.com.tributos.iss.adapters.in.web;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iss.adapters.in.web.dto.ContribuinteResponse;
import br.com.tributos.iss.adapters.in.web.dto.SalvarContribuinteRequest;
import br.com.tributos.iss.application.BuscarContribuinteService;
import br.com.tributos.iss.application.ExcluirContribuinteService;
import br.com.tributos.iss.application.ListarContribuintesService;
import br.com.tributos.iss.application.SalvarContribuinteComando;
import br.com.tributos.iss.application.SalvarContribuinteService;
import br.com.tributos.iss.domain.Contribuinte;

@RestController
@RequestMapping("/api/iss/contribuintes")
public class ContribuinteController {

    private final ListarContribuintesService listarContribuintesService;
    private final BuscarContribuinteService buscarContribuinteService;
    private final SalvarContribuinteService salvarContribuinteService;
    private final ExcluirContribuinteService excluirContribuinteService;

    public ContribuinteController(
        ListarContribuintesService listarContribuintesService,
        BuscarContribuinteService buscarContribuinteService,
        SalvarContribuinteService salvarContribuinteService,
        ExcluirContribuinteService excluirContribuinteService
    ) {
        this.listarContribuintesService = listarContribuintesService;
        this.buscarContribuinteService = buscarContribuinteService;
        this.salvarContribuinteService = salvarContribuinteService;
        this.excluirContribuinteService = excluirContribuinteService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<ContribuinteResumoResponse> listar(
        @RequestParam(required = false) String busca,
        Pageable pageable
    ) {
        return listarContribuintesService.executar(busca, pageable).map(ContribuinteResumoResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/{id}")
    public ContribuinteResponse buscar(@PathVariable UUID id) {
        return ContribuinteResponse.de(buscarContribuinteService.executar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<ContribuinteResponse> criar(@Valid @RequestBody SalvarContribuinteRequest request) {
        Contribuinte contribuinte = salvarContribuinteService.executar(paraComando(request), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(ContribuinteResponse.de(contribuinte));
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PutMapping("/{id}")
    public ContribuinteResponse atualizar(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarContribuinteRequest request
    ) {
        return ContribuinteResponse.de(salvarContribuinteService.executar(paraComando(request), id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirContribuinteService.executar(id);
        return ResponseEntity.noContent().build();
    }

    private static SalvarContribuinteComando paraComando(SalvarContribuinteRequest request) {
        return new SalvarContribuinteComando(
            request.pessoaId(),
            request.inscricaoMunicipal(),
            request.tipoContribuinteId(),
            request.situacaoCadastralId(),
            request.regimeTributarioId(),
            request.nomeContador(),
            request.emailContador()
        );
    }

    public record ContribuinteResumoResponse(UUID id, UUID pessoaId, String inscricaoMunicipal, UUID statusCredenciamentoId) {
        static ContribuinteResumoResponse de(Contribuinte contribuinte) {
            return new ContribuinteResumoResponse(
                contribuinte.id(),
                contribuinte.pessoaId(),
                contribuinte.inscricaoMunicipal(),
                contribuinte.statusCredenciamentoId()
            );
        }
    }
}
