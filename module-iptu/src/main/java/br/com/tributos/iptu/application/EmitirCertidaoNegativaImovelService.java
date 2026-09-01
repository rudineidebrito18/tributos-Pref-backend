package br.com.tributos.iptu.application;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.CertidaoNegativaImovel;
import br.com.tributos.iptu.domain.CertidaoNegativaImovelRepository;
import br.com.tributos.iptu.domain.EmitirCertidaoNegativaComando;
import br.com.tributos.iptu.domain.GeradorCodigoVerificacao;
import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.ValidadorVigenciaDocumento;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.RegraNegocioException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.financeiro.PendenciaFinanceiraPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class EmitirCertidaoNegativaImovelService {

    private static final int DIAS_VALIDADE_PADRAO = 90;

    private final CertidaoNegativaImovelRepository certidaoNegativaImovelRepository;
    private final ImovelRepository imovelRepository;
    private final PendenciaFinanceiraPort pendenciaFinanceiraPort;
    private final ProprietarioPrincipalImovelService proprietarioPrincipalImovelService;

    public EmitirCertidaoNegativaImovelService(
        CertidaoNegativaImovelRepository certidaoNegativaImovelRepository,
        ImovelRepository imovelRepository,
        PendenciaFinanceiraPort pendenciaFinanceiraPort,
        ProprietarioPrincipalImovelService proprietarioPrincipalImovelService
    ) {
        this.certidaoNegativaImovelRepository = certidaoNegativaImovelRepository;
        this.imovelRepository = imovelRepository;
        this.pendenciaFinanceiraPort = pendenciaFinanceiraPort;
        this.proprietarioPrincipalImovelService = proprietarioPrincipalImovelService;
    }

    @Transactional
    public CertidaoNegativaImovel executar(UUID imovelId, EmitirCertidaoNegativaComando comando) {
        Imovel imovel = imovelRepository.buscarPorId(imovelId)
            .orElseThrow(() -> new NotFoundException("Imóvel não encontrado."));

        if (comando == null || comando.situacaoCndId() == null) {
            throw new ValidationException("Informe a situação da certidão negativa.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        UUID pessoaProprietaria = proprietarioPrincipalImovelService.buscarPessoaIdPrincipal(imovelId)
            .orElseThrow(() -> new ValidationException("Imóvel sem proprietário principal cadastrado."));
        validarPendencias(tenantId, pessoaProprietaria);

        LocalDate dataEmissao = LocalDate.now();
        LocalDate validade = comando.validade() != null
            ? comando.validade()
            : dataEmissao.plusDays(DIAS_VALIDADE_PADRAO);

        ValidadorVigenciaDocumento.validarPeriodoCertidao(dataEmissao, validade);

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
            dataEmissaoTs,
            comando.situacaoCndId(),
            comando.observacao()
        );

        return certidaoNegativaImovelRepository.salvar(certidao);
    }

    /** Compatibilidade com chamadas legadas sem corpo. */
    @Transactional
    public CertidaoNegativaImovel executar(UUID imovelId, LocalDate validadeOverride) {
        throw new ValidationException("Informe a situação da certidão negativa.");
    }

    private void validarPendencias(UUID tenantId, UUID pessoaId) {
        if (pendenciaFinanceiraPort.possuiPendenciaTributo(tenantId, pessoaId, "IPTU")
            || pendenciaFinanceiraPort.possuiPendenciaTributo(tenantId, pessoaId, "ITBI")) {
            throw new RegraNegocioException(
                "Não é possível emitir certidão: existem pendências financeiras de IPTU ou ITBI para o imóvel."
            );
        }
    }
}
