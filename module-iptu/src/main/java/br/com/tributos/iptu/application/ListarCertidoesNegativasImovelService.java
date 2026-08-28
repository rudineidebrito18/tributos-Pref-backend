package br.com.tributos.iptu.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.CertidaoNegativaImovel;
import br.com.tributos.iptu.domain.CertidaoNegativaImovelRepository;

@Service
public class ListarCertidoesNegativasImovelService {

    private final CertidaoNegativaImovelRepository certidaoNegativaImovelRepository;

    public ListarCertidoesNegativasImovelService(CertidaoNegativaImovelRepository certidaoNegativaImovelRepository) {
        this.certidaoNegativaImovelRepository = certidaoNegativaImovelRepository;
    }

    @Transactional(readOnly = true)
    public Page<CertidaoNegativaImovel> executar(UUID imovelId, Pageable pageable) {
        return certidaoNegativaImovelRepository.listarPorImovel(imovelId, pageable);
    }
}
