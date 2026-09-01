package br.com.tributos.iptu.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.adapters.in.web.dto.ImovelResponse;
import br.com.tributos.iptu.domain.Imovel;

@Service
public class MontarImovelResponseService {

    private final ProprietarioPrincipalImovelService proprietarioPrincipalImovelService;

    public MontarImovelResponseService(ProprietarioPrincipalImovelService proprietarioPrincipalImovelService) {
        this.proprietarioPrincipalImovelService = proprietarioPrincipalImovelService;
    }

    @Transactional(readOnly = true)
    public ImovelResponse montar(Imovel imovel) {
        UUID proprietarioPessoaId = proprietarioPrincipalImovelService.buscarPessoaIdPrincipal(imovel.id()).orElse(null);
        return ImovelResponse.de(imovel, proprietarioPessoaId);
    }
}
