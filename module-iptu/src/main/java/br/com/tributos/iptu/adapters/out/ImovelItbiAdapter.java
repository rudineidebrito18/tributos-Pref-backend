package br.com.tributos.iptu.adapters.out;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.application.CalculadorIptu;
import br.com.tributos.iptu.application.ProprietarioPrincipalImovelService;
import br.com.tributos.iptu.domain.ContribuinteReferenciaRepository;
import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelProprietario;
import br.com.tributos.iptu.domain.ImovelProprietarioRepository;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.ImovelTitularidadeHistorico;
import br.com.tributos.iptu.domain.ImovelTitularidadeHistoricoRepository;
import br.com.tributos.iptu.domain.SituacaoImovel;
import br.com.tributos.iptu.domain.TipoRegistroTitularidade;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.iptu.ImovelItbiPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ImovelItbiAdapter implements ImovelItbiPort {

    private final ImovelRepository imovelRepository;
    private final ProprietarioPrincipalImovelService proprietarioPrincipalImovelService;
    private final ImovelProprietarioRepository imovelProprietarioRepository;
    private final ImovelTitularidadeHistoricoRepository titularidadeHistoricoRepository;
    private final ContribuinteReferenciaRepository contribuinteReferenciaRepository;

    public ImovelItbiAdapter(
        ImovelRepository imovelRepository,
        ProprietarioPrincipalImovelService proprietarioPrincipalImovelService,
        ImovelProprietarioRepository imovelProprietarioRepository,
        ImovelTitularidadeHistoricoRepository titularidadeHistoricoRepository,
        ContribuinteReferenciaRepository contribuinteReferenciaRepository
    ) {
        this.imovelRepository = imovelRepository;
        this.proprietarioPrincipalImovelService = proprietarioPrincipalImovelService;
        this.imovelProprietarioRepository = imovelProprietarioRepository;
        this.titularidadeHistoricoRepository = titularidadeHistoricoRepository;
        this.contribuinteReferenciaRepository = contribuinteReferenciaRepository;
    }

    @Override
    public ImovelItbiDados buscarDados(UUID imovelId) {
        Imovel imovel = imovelRepository.buscarPorId(imovelId)
            .orElseThrow(() -> new NotFoundException("Imóvel não encontrado."));
        UUID proprietarioPessoaId = proprietarioPrincipalImovelService.buscarPessoaIdPrincipal(imovelId)
            .orElseThrow(() -> new ValidationException("Imóvel sem proprietário principal cadastrado."));
        return new ImovelItbiDados(
            imovel.id(),
            proprietarioPessoaId,
            CalculadorIptu.calcularValorVenal(imovel),
            imovel.situacao() == SituacaoImovel.ATIVO
        );
    }

    @Override
    public void transferirTitularidade(UUID imovelId, UUID novoProprietarioPessoaId) {
        UUID contribuinteId = contribuinteReferenciaRepository.buscarContribuinteIdPorPessoaId(novoProprietarioPessoaId)
            .orElseThrow(() -> new ValidationException("Novo proprietário não encontrado no cadastro de contribuintes."));

        if (imovelRepository.buscarPorId(imovelId).isEmpty()) {
            throw new NotFoundException("Imóvel não encontrado.");
        }

        imovelProprietarioRepository.removerPorImovel(imovelId);
        UUID tenantId = TenantContext.getObrigatorio();
        imovelProprietarioRepository.salvar(new ImovelProprietario(
            UUID.randomUUID(),
            tenantId,
            imovelId,
            contribuinteId,
            java.math.BigDecimal.valueOf(100),
            true
        ));
    }

    @Override
    public void transferirTitularidadePorPartes(
        UUID imovelId,
        List<ParteTransferencia> transmitentes,
        List<ParteTransferencia> adquirentes
    ) {
        if (imovelRepository.buscarPorId(imovelId).isEmpty()) {
            throw new NotFoundException("Imóvel não encontrado.");
        }

        UUID tenantId = TenantContext.getObrigatorio();

        for (ParteTransferencia transmitente : transmitentes) {
            titularidadeHistoricoRepository.salvar(new ImovelTitularidadeHistorico(
                UUID.randomUUID(),
                tenantId,
                imovelId,
                transmitente.contribuinteId(),
                TipoRegistroTitularidade.SAIDA,
                transmitente.porcentagem(),
                null
            ));
        }

        for (ParteTransferencia adquirente : adquirentes) {
            titularidadeHistoricoRepository.salvar(new ImovelTitularidadeHistorico(
                UUID.randomUUID(),
                tenantId,
                imovelId,
                adquirente.contribuinteId(),
                TipoRegistroTitularidade.ENTRADA,
                adquirente.porcentagem(),
                null
            ));
        }

        imovelProprietarioRepository.removerPorImovel(imovelId);

        for (ParteTransferencia adquirente : adquirentes) {
            imovelProprietarioRepository.salvar(new ImovelProprietario(
                UUID.randomUUID(),
                tenantId,
                imovelId,
                adquirente.contribuinteId(),
                adquirente.porcentagem(),
                adquirente.principal()
            ));
        }
    }
}
