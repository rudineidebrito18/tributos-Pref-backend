package br.com.tributos.iptu.application;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.CertidaoNegativaImovel;
import br.com.tributos.iptu.domain.CertidaoNegativaImovelRepository;
import br.com.tributos.iptu.domain.GeradorCodigoVerificacao;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.ValidadorVigenciaDocumento;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class EmitirCertidaoNegativaImovelService {

    private static final int DIAS_VALIDADE_PADRAO = 90;

    private final CertidaoNegativaImovelRepository certidaoNegativaImovelRepository;
    private final ImovelRepository imovelRepository;

    public EmitirCertidaoNegativaImovelService(
        CertidaoNegativaImovelRepository certidaoNegativaImovelRepository,
        ImovelRepository imovelRepository
    ) {
        this.certidaoNegativaImovelRepository = certidaoNegativaImovelRepository;
        this.imovelRepository = imovelRepository;
    }

    @Transactional
    public CertidaoNegativaImovel executar(UUID imovelId, LocalDate validadeOverride) {
        if (!imovelRepository.buscarPorId(imovelId).isPresent()) {
            throw new NotFoundException("Imóvel não encontrado.");
        }

        LocalDate dataEmissao = LocalDate.now();
        LocalDate validade = validadeOverride != null
            ? validadeOverride
            : dataEmissao.plusDays(DIAS_VALIDADE_PADRAO);

        ValidadorVigenciaDocumento.validarPeriodoCertidao(dataEmissao, validade);

        UUID tenantId = TenantContext.getObrigatorio();
        long numero = certidaoNegativaImovelRepository.proximoNumero();
        String codigoVerificacao = GeradorCodigoVerificacao.gerar();
        Instant dataEmissaoTs = Instant.now();

        CertidaoNegativaImovel certidao = new CertidaoNegativaImovel(
            UUID.randomUUID(),
            tenantId,
            imovelId,
            numero,
            dataEmissao,
            validade,
            codigoVerificacao,
            dataEmissaoTs
        );

        return certidaoNegativaImovelRepository.salvar(certidao);
    }
}
