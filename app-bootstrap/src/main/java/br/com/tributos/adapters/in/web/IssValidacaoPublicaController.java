package br.com.tributos.adapters.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.identity.application.BuscarTenantPorSlugService;
import br.com.tributos.iss.adapters.in.web.dto.ValidacaoDocumentoResponse;
import br.com.tributos.iss.application.ValidarDocumentoIssService;
import br.com.tributos.kernel.tenancy.TenantContext;

/**
 * Endpoint público de validação de alvarás e certidões ISS por código de verificação.
 */
@RestController
@RequestMapping("/api/public/tenants")
public class IssValidacaoPublicaController {

    private final BuscarTenantPorSlugService buscarTenantPorSlugService;
    private final ValidarDocumentoIssService validarDocumentoIssService;

    public IssValidacaoPublicaController(
        BuscarTenantPorSlugService buscarTenantPorSlugService,
        ValidarDocumentoIssService validarDocumentoIssService
    ) {
        this.buscarTenantPorSlugService = buscarTenantPorSlugService;
        this.validarDocumentoIssService = validarDocumentoIssService;
    }

    @GetMapping("/{slug}/iss/validar/{codigo}")
    public ValidacaoDocumentoResponse validar(@PathVariable String slug, @PathVariable String codigo) {
        try {
            TenantContext.set(buscarTenantPorSlugService.executar(slug).getId());
            return ValidacaoDocumentoResponse.de(validarDocumentoIssService.executar(codigo));
        } finally {
            TenantContext.clear();
        }
    }
}
