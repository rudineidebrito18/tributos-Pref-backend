package br.com.tributos.identity.adapters.in.web;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.identity.adapters.in.web.dto.ConfirmarMfaRequest;
import br.com.tributos.identity.adapters.in.web.dto.LoginRequest;
import br.com.tributos.identity.adapters.in.web.dto.LoginResponse;
import br.com.tributos.identity.adapters.in.web.dto.RefreshRequest;
import br.com.tributos.identity.adapters.in.web.dto.SegredoMfaResponse;
import br.com.tributos.identity.adapters.in.web.dto.TokensResponse;
import br.com.tributos.identity.adapters.in.web.dto.VerificarMfaLoginRequest;
import br.com.tributos.identity.application.AutenticarUsuarioService;
import br.com.tributos.identity.application.ConfirmarDesafioMfaService;
import br.com.tributos.identity.application.ConfirmarHabilitacaoMfaService;
import br.com.tributos.identity.application.HabilitarMfaService;
import br.com.tributos.identity.application.RenovarTokenService;
import br.com.tributos.identity.application.RevogarSessaoService;
import br.com.tributos.identity.domain.Tenant;
import br.com.tributos.identity.domain.TenantRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;

/**
 * Login em duas etapas (senha, depois MFA se habilitado), refresh/rotação de sessão e
 * habilitação de TOTP — ver PLANEJAMENTO_PROJETO.md §9 e ROADMAP §Sprint 0. Endpoints
 * públicos e autenticados convivem aqui porque, do ponto de vista do cliente, são todos
 * "fluxo de autenticação"; o que é público ou não está listado em
 * {@code SecurityConfig.CAMINHOS_PUBLICOS}.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String CABECALHO_TENANT_SLUG = "X-Tenant-Slug";

    private final TenantRepository tenantRepository;
    private final AutenticarUsuarioService autenticarUsuarioService;
    private final ConfirmarDesafioMfaService confirmarDesafioMfaService;
    private final RenovarTokenService renovarTokenService;
    private final RevogarSessaoService revogarSessaoService;
    private final HabilitarMfaService habilitarMfaService;
    private final ConfirmarHabilitacaoMfaService confirmarHabilitacaoMfaService;

    public AuthController(
        TenantRepository tenantRepository,
        AutenticarUsuarioService autenticarUsuarioService,
        ConfirmarDesafioMfaService confirmarDesafioMfaService,
        RenovarTokenService renovarTokenService,
        RevogarSessaoService revogarSessaoService,
        HabilitarMfaService habilitarMfaService,
        ConfirmarHabilitacaoMfaService confirmarHabilitacaoMfaService
    ) {
        this.tenantRepository = tenantRepository;
        this.autenticarUsuarioService = autenticarUsuarioService;
        this.confirmarDesafioMfaService = confirmarDesafioMfaService;
        this.renovarTokenService = renovarTokenService;
        this.revogarSessaoService = revogarSessaoService;
        this.habilitarMfaService = habilitarMfaService;
        this.confirmarHabilitacaoMfaService = confirmarHabilitacaoMfaService;
    }

    /**
     * Tenant vem do header {@code X-Tenant-Slug} (mesmo header que o
     * {@code middleware.ts} do frontend resolve a partir do subdomínio) — o corpo da
     * requisição nunca carrega o tenant, pra não dar a um cliente mal-intencionado a
     * chance de tentar logar "como" outro tenant só trocando um campo do JSON.
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestHeader(CABECALHO_TENANT_SLUG) String tenantSlug, @Valid @RequestBody LoginRequest request) {
        UUID tenantId = resolverTenantId(tenantSlug);
        return LoginResponse.de(autenticarUsuarioService.executar(tenantId, request.login(), request.senha()));
    }

    @PostMapping("/mfa/verificar")
    public TokensResponse verificarMfaDoLogin(@Valid @RequestBody VerificarMfaLoginRequest request) {
        return TokensResponse.de(confirmarDesafioMfaService.executar(request.tokenMfaPendente(), request.codigo()));
    }

    @PostMapping("/refresh")
    public TokensResponse renovarSessao(@Valid @RequestBody RefreshRequest request) {
        return TokensResponse.de(renovarTokenService.executar(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        revogarSessaoService.executar(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    /** Autenticado — {@code jwt.getSubject()} é o id do usuário (claim {@code sub}, ver JwtGeradorToken). */
    @PostMapping("/mfa/habilitar")
    public SegredoMfaResponse habilitarMfa(@AuthenticationPrincipal Jwt jwt) {
        return SegredoMfaResponse.de(habilitarMfaService.executar(UUID.fromString(jwt.getSubject())));
    }

    @PostMapping("/mfa/confirmar")
    public ResponseEntity<Void> confirmarHabilitacaoMfa(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ConfirmarMfaRequest request) {
        confirmarHabilitacaoMfaService.executar(UUID.fromString(jwt.getSubject()), request.codigo());
        return ResponseEntity.noContent().build();
    }

    private UUID resolverTenantId(String slug) {
        return tenantRepository.buscarPorSlug(slug)
            .filter(Tenant::isAtivo)
            .map(Tenant::getId)
            .orElseThrow(() -> new AutenticacaoException("Tenant inválido."));
    }
}
