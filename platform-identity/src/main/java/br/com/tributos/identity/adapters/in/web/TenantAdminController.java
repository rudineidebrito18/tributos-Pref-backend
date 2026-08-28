package br.com.tributos.identity.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.identity.adapters.in.web.dto.CriarTenantRequest;
import br.com.tributos.identity.adapters.in.web.dto.TenantCriadoResponse;
import br.com.tributos.identity.application.CriarTenantComando;
import br.com.tributos.identity.application.CriarTenantService;
import br.com.tributos.identity.domain.PaletaTenant;
import br.com.tributos.identity.domain.TipoEntidade;
import br.com.tributos.kernel.exception.ValidationException;

/**
 * Onboarding de novas prefeituras/câmaras (tenants) — operação da EQUIPE DA PLATAFORMA,
 * não de uma prefeitura específica. {@code @PreAuthorize} aqui é a linha real de defesa:
 * o papel PLATAFORMA_ADMIN existe fora do escopo de qualquer tenant de negócio (ver
 * V5__tenant_dominio_e_plataforma_admin.sql) e não enxerga dado de nenhuma prefeitura —
 * só cria o registro do tenant e seu primeiro usuário ADMIN_TENANT.
 */
@RestController
@RequestMapping("/api/admin/tenants")
public class TenantAdminController {

    private final CriarTenantService criarTenantService;

    public TenantAdminController(CriarTenantService criarTenantService) {
        this.criarTenantService = criarTenantService;
    }

    @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
    @PostMapping
    public ResponseEntity<TenantCriadoResponse> criar(@Valid @RequestBody CriarTenantRequest request) {
        CriarTenantComando comando = new CriarTenantComando(
            request.slug(),
            request.nome(),
            request.uf().toUpperCase(),
            tipoEntidadeDe(request.tipoEntidade()),
            request.logoUrl(),
            paletaDe(request.cores()),
            request.modulosAtivos(),
            request.dominioProprio(),
            request.loginAdminInicial(),
            request.emailAdminInicial()
        );

        TenantCriadoResponse resposta = TenantCriadoResponse.de(criarTenantService.executar(comando));
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    private static TipoEntidade tipoEntidadeDe(String valor) {
        try {
            return TipoEntidade.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Tipo de entidade inválido: \"" + valor + "\" (use PREFEITURA ou CAMARA).");
        }
    }

    private static PaletaTenant paletaDe(CriarTenantRequest.Cores cores) {
        return cores == null
            ? null
            : new PaletaTenant(cores.accent(), cores.accentDark(), cores.accentSecondary(), cores.accentTertiary());
    }
}
