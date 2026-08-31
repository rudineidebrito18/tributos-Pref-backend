package br.com.tributos.iss.adapters.in.web;

import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iss.adapters.in.web.dto.GrupoServicoResponse;
import br.com.tributos.iss.adapters.in.web.dto.LocalIncidenciaResponse;
import br.com.tributos.iss.adapters.in.web.dto.SalvarGrupoServicoRequest;
import br.com.tributos.iss.adapters.in.web.dto.SalvarLocalIncidenciaRequest;
import br.com.tributos.iss.adapters.in.web.dto.SalvarSituacaoCndRequest;
import br.com.tributos.iss.adapters.in.web.dto.SalvarStatusSolicitacaoRequest;
import br.com.tributos.iss.adapters.in.web.dto.SalvarTipoSolicitacaoRequest;
import br.com.tributos.iss.adapters.in.web.dto.SituacaoCndResponse;
import br.com.tributos.iss.adapters.in.web.dto.StatusSolicitacaoResponse;
import br.com.tributos.iss.adapters.in.web.dto.TipoSolicitacaoResponse;
import br.com.tributos.iss.application.GerenciarApoioIssService;

@RestController
@RequestMapping("/api/iss")
public class ApoioIssController {

    private final GerenciarApoioIssService gerenciarApoioIssService;

    public ApoioIssController(GerenciarApoioIssService gerenciarApoioIssService) {
        this.gerenciarApoioIssService = gerenciarApoioIssService;
    }

    // --- Situações CND ---

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/situacoes-cnd")
    public List<SituacaoCndResponse> listarSituacoesCnd() {
        return gerenciarApoioIssService.listarSituacoesCnd().stream().map(SituacaoCndResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/situacoes-cnd/{id}")
    public SituacaoCndResponse buscarSituacaoCnd(@PathVariable UUID id) {
        return SituacaoCndResponse.de(gerenciarApoioIssService.buscarSituacaoCnd(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/situacoes-cnd")
    public ResponseEntity<SituacaoCndResponse> criarSituacaoCnd(
        @Valid @RequestBody SalvarSituacaoCndRequest request
    ) {
        SituacaoCndResponse resposta = SituacaoCndResponse.de(
            gerenciarApoioIssService.criarSituacaoCnd(request.descricao(), request.titulo(), request.ativo())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/situacoes-cnd/{id}")
    public SituacaoCndResponse atualizarSituacaoCnd(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarSituacaoCndRequest request
    ) {
        return SituacaoCndResponse.de(
            gerenciarApoioIssService.atualizarSituacaoCnd(id, request.descricao(), request.titulo(), request.ativo())
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/situacoes-cnd/{id}")
    public ResponseEntity<Void> excluirSituacaoCnd(@PathVariable UUID id) {
        gerenciarApoioIssService.excluirSituacaoCnd(id);
        return ResponseEntity.noContent().build();
    }

    // --- Tipos de Solicitação ---

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/tipos-solicitacao")
    public List<TipoSolicitacaoResponse> listarTiposSolicitacao() {
        return gerenciarApoioIssService.listarTiposSolicitacao().stream().map(TipoSolicitacaoResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/tipos-solicitacao/{id}")
    public TipoSolicitacaoResponse buscarTipoSolicitacao(@PathVariable UUID id) {
        return TipoSolicitacaoResponse.de(gerenciarApoioIssService.buscarTipoSolicitacao(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/tipos-solicitacao")
    public ResponseEntity<TipoSolicitacaoResponse> criarTipoSolicitacao(
        @Valid @RequestBody SalvarTipoSolicitacaoRequest request
    ) {
        TipoSolicitacaoResponse resposta = TipoSolicitacaoResponse.de(
            gerenciarApoioIssService.criarTipoSolicitacao(
                request.descricao(), request.usuarioNotificarId(), request.ativo()
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/tipos-solicitacao/{id}")
    public TipoSolicitacaoResponse atualizarTipoSolicitacao(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarTipoSolicitacaoRequest request
    ) {
        return TipoSolicitacaoResponse.de(
            gerenciarApoioIssService.atualizarTipoSolicitacao(
                id, request.descricao(), request.usuarioNotificarId(), request.ativo()
            )
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/tipos-solicitacao/{id}")
    public ResponseEntity<Void> excluirTipoSolicitacao(@PathVariable UUID id) {
        gerenciarApoioIssService.excluirTipoSolicitacao(id);
        return ResponseEntity.noContent().build();
    }

    // --- Status de Solicitação ---

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/status-solicitacao")
    public List<StatusSolicitacaoResponse> listarStatusSolicitacao() {
        return gerenciarApoioIssService.listarStatusSolicitacao().stream().map(StatusSolicitacaoResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/status-solicitacao/{id}")
    public StatusSolicitacaoResponse buscarStatusSolicitacao(@PathVariable UUID id) {
        return StatusSolicitacaoResponse.de(gerenciarApoioIssService.buscarStatusSolicitacao(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/status-solicitacao")
    public ResponseEntity<StatusSolicitacaoResponse> criarStatusSolicitacao(
        @Valid @RequestBody SalvarStatusSolicitacaoRequest request
    ) {
        StatusSolicitacaoResponse resposta = StatusSolicitacaoResponse.de(
            gerenciarApoioIssService.criarStatusSolicitacao(request.descricao(), request.ativo())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/status-solicitacao/{id}")
    public StatusSolicitacaoResponse atualizarStatusSolicitacao(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarStatusSolicitacaoRequest request
    ) {
        return StatusSolicitacaoResponse.de(
            gerenciarApoioIssService.atualizarStatusSolicitacao(id, request.descricao(), request.ativo())
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/status-solicitacao/{id}")
    public ResponseEntity<Void> excluirStatusSolicitacao(@PathVariable UUID id) {
        gerenciarApoioIssService.excluirStatusSolicitacao(id);
        return ResponseEntity.noContent().build();
    }

    // --- Locais de Incidência ---

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/locais-incidencia")
    public List<LocalIncidenciaResponse> listarLocaisIncidencia() {
        return gerenciarApoioIssService.listarLocaisIncidencia().stream().map(LocalIncidenciaResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/locais-incidencia/{id}")
    public LocalIncidenciaResponse buscarLocalIncidencia(@PathVariable UUID id) {
        return LocalIncidenciaResponse.de(gerenciarApoioIssService.buscarLocalIncidencia(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/locais-incidencia")
    public ResponseEntity<LocalIncidenciaResponse> criarLocalIncidencia(
        @Valid @RequestBody SalvarLocalIncidenciaRequest request
    ) {
        LocalIncidenciaResponse resposta = LocalIncidenciaResponse.de(
            gerenciarApoioIssService.criarLocalIncidencia(request.descricao(), request.ativo())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/locais-incidencia/{id}")
    public LocalIncidenciaResponse atualizarLocalIncidencia(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarLocalIncidenciaRequest request
    ) {
        return LocalIncidenciaResponse.de(
            gerenciarApoioIssService.atualizarLocalIncidencia(id, request.descricao(), request.ativo())
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/locais-incidencia/{id}")
    public ResponseEntity<Void> excluirLocalIncidencia(@PathVariable UUID id) {
        gerenciarApoioIssService.excluirLocalIncidencia(id);
        return ResponseEntity.noContent().build();
    }

    // --- Grupos de Serviço ---

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/grupos-servico")
    public List<GrupoServicoResponse> listarGruposServico() {
        return gerenciarApoioIssService.listarGruposServico().stream().map(GrupoServicoResponse::de).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/grupos-servico/{id}")
    public GrupoServicoResponse buscarGrupoServico(@PathVariable UUID id) {
        return GrupoServicoResponse.de(gerenciarApoioIssService.buscarGrupoServico(id));
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping("/grupos-servico")
    public ResponseEntity<GrupoServicoResponse> criarGrupoServico(
        @Valid @RequestBody SalvarGrupoServicoRequest request
    ) {
        GrupoServicoResponse resposta = GrupoServicoResponse.de(
            gerenciarApoioIssService.criarGrupoServico(request.codigo(), request.descricao())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PutMapping("/grupos-servico/{id}")
    public GrupoServicoResponse atualizarGrupoServico(
        @PathVariable UUID id,
        @Valid @RequestBody SalvarGrupoServicoRequest request
    ) {
        return GrupoServicoResponse.de(
            gerenciarApoioIssService.atualizarGrupoServico(id, request.codigo(), request.descricao())
        );
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @DeleteMapping("/grupos-servico/{id}")
    public ResponseEntity<Void> excluirGrupoServico(@PathVariable UUID id) {
        gerenciarApoioIssService.excluirGrupoServico(id);
        return ResponseEntity.noContent().build();
    }
}
