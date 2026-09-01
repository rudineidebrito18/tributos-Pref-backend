package br.com.tributos.itbi.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.itbi.adapters.in.web.dto.GuiaItbiResponse;
import br.com.tributos.itbi.domain.GuiaItbi;

@Service
public class MontarGuiaItbiResponseService {

    private final AdquirentePrincipalGuiaItbiService adquirentePrincipalGuiaItbiService;

    public MontarGuiaItbiResponseService(AdquirentePrincipalGuiaItbiService adquirentePrincipalGuiaItbiService) {
        this.adquirentePrincipalGuiaItbiService = adquirentePrincipalGuiaItbiService;
    }

    @Transactional(readOnly = true)
    public GuiaItbiResponse montar(GuiaItbi guia) {
        return GuiaItbiResponse.de(
            guia,
            adquirentePrincipalGuiaItbiService.buscarPessoaIdPrincipal(guia.id()).orElse(null)
        );
    }
}
