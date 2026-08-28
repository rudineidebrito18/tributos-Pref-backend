package br.com.tributos.adapters.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.application.portal.ConsultarSituacaoFiscalPublicaService;
import br.com.tributos.application.portal.SegundaViaGuiaPublicaService;
import br.com.tributos.identity.application.BuscarTenantPorSlugService;
import br.com.tributos.kernel.tenancy.TenantContext;

/**
 * Portal do contribuinte — endpoints públicos (rate-limited) para autoatendimento.
 */
@RestController
@RequestMapping("/api/public/tenants")
public class PortalContribuintePublicController {

    private final BuscarTenantPorSlugService buscarTenantPorSlugService;
    private final ConsultarSituacaoFiscalPublicaService consultarSituacaoFiscalPublicaService;
    private final SegundaViaGuiaPublicaService segundaViaGuiaPublicaService;

    public PortalContribuintePublicController(
        BuscarTenantPorSlugService buscarTenantPorSlugService,
        ConsultarSituacaoFiscalPublicaService consultarSituacaoFiscalPublicaService,
        SegundaViaGuiaPublicaService segundaViaGuiaPublicaService
    ) {
        this.buscarTenantPorSlugService = buscarTenantPorSlugService;
        this.consultarSituacaoFiscalPublicaService = consultarSituacaoFiscalPublicaService;
        this.segundaViaGuiaPublicaService = segundaViaGuiaPublicaService;
    }

    @GetMapping("/{slug}/contribuinte/situacao-fiscal")
    public ConsultarSituacaoFiscalPublicaService.SituacaoFiscalPublica situacaoFiscal(
        @PathVariable String slug,
        @RequestParam String cpfCnpj
    ) {
        return comTenant(slug, () -> consultarSituacaoFiscalPublicaService.executar(cpfCnpj));
    }

    @GetMapping("/{slug}/financeiro/guias/{numero}/segunda-via")
    public SegundaViaGuiaPublicaService.SegundaViaGuiaPublica segundaVia(
        @PathVariable String slug,
        @PathVariable long numero,
        @RequestParam String cpfCnpj
    ) {
        return comTenant(slug, () -> segundaViaGuiaPublicaService.executar(numero, cpfCnpj));
    }

    private <T> T comTenant(String slug, java.util.function.Supplier<T> acao) {
        try {
            TenantContext.set(buscarTenantPorSlugService.executar(slug).getId());
            return acao.get();
        } finally {
            TenantContext.clear();
        }
    }
}
