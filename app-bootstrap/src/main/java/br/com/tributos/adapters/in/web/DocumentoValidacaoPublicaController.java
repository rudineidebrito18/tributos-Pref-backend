package br.com.tributos.adapters.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.adapters.in.web.dto.ValidacaoDocumentoPublicoResponse;
import br.com.tributos.application.portal.ValidarDocumentoPublicoService;
import br.com.tributos.identity.application.BuscarTenantPorSlugService;
import br.com.tributos.kernel.tenancy.TenantContext;

@RestController
@RequestMapping("/api/public/tenants")
public class DocumentoValidacaoPublicaController {

    private final BuscarTenantPorSlugService buscarTenantPorSlugService;
    private final ValidarDocumentoPublicoService validarDocumentoPublicoService;

    public DocumentoValidacaoPublicaController(
        BuscarTenantPorSlugService buscarTenantPorSlugService,
        ValidarDocumentoPublicoService validarDocumentoPublicoService
    ) {
        this.buscarTenantPorSlugService = buscarTenantPorSlugService;
        this.validarDocumentoPublicoService = validarDocumentoPublicoService;
    }

    @GetMapping("/{slug}/documentos/validar/{codigo}")
    public ValidacaoDocumentoPublicoResponse validar(@PathVariable String slug, @PathVariable String codigo) {
        try {
            TenantContext.set(buscarTenantPorSlugService.executar(slug).getId());
            return ValidacaoDocumentoPublicoResponse.de(validarDocumentoPublicoService.executar(codigo));
        } finally {
            TenantContext.clear();
        }
    }
}
