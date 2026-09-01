package br.com.tributos.iptu.adapters.out;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.application.CalculadorIptu;
import br.com.tributos.iptu.domain.ContribuinteReferenciaRepository;
import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelProprietario;
import br.com.tributos.iptu.domain.ImovelProprietarioRepository;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.ImovelTitularidadeHistorico;
import br.com.tributos.iptu.domain.ImovelTitularidadeHistoricoRepository;
import br.com.tributos.iptu.domain.PessoaReferenciaRepository;
import br.com.tributos.iptu.domain.SituacaoImovel;
import br.com.tributos.iptu.domain.TipoRegistroTitularidade;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.iptu.ImovelItbiPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ImovelItbiAdapter implements ImovelItbiPort {

    private final ImovelRepository imovelRepository;
    private final PessoaReferenciaRepository pessoaReferenciaRepository;
    private final ImovelProprietarioRepository imovelProprietarioRepository;
    private final ImovelTitularidadeHistoricoRepository titularidadeHistoricoRepository;
    private final ContribuinteReferenciaRepository contribuinteReferenciaRepository;

    public ImovelItbiAdapter(
        ImovelRepository imovelRepository,
        PessoaReferenciaRepository pessoaReferenciaRepository,
        ImovelProprietarioRepository imovelProprietarioRepository,
        ImovelTitularidadeHistoricoRepository titularidadeHistoricoRepository,
        ContribuinteReferenciaRepository contribuinteReferenciaRepository
    ) {
        this.imovelRepository = imovelRepository;
        this.pessoaReferenciaRepository = pessoaReferenciaRepository;
        this.imovelProprietarioRepository = imovelProprietarioRepository;
        this.titularidadeHistoricoRepository = titularidadeHistoricoRepository;
        this.contribuinteReferenciaRepository = contribuinteReferenciaRepository;
    }

    @Override
    public ImovelItbiDados buscarDados(UUID imovelId) {
        Imovel imovel = imovelRepository.buscarPorId(imovelId)
            .orElseThrow(() -> new NotFoundException("Imóvel não encontrado."));
        return new ImovelItbiDados(
            imovel.id(),
            imovel.proprietarioId(),
            CalculadorIptu.calcularValorVenal(imovel),
            imovel.situacao() == SituacaoImovel.ATIVO
        );
    }

    @Override
    public void transferirTitularidade(UUID imovelId, UUID novoProprietarioId) {
        if (!pessoaReferenciaRepository.existe(novoProprietarioId)) {
            throw new ValidationException("Novo proprietário não encontrado no cadastro.");
        }

        Imovel imovel = imovelRepository.buscarPorId(imovelId)
            .orElseThrow(() -> new NotFoundException("Imóvel não encontrado."));

        imovelRepository.salvar(atualizarProprietarioLegado(imovel, novoProprietarioId));
    }

    @Override
    public void transferirTitularidadePorPartes(
        UUID imovelId,
        List<ParteTransferencia> transmitentes,
        List<ParteTransferencia> adquirentes
    ) {
        Imovel imovel = imovelRepository.buscarPorId(imovelId)
            .orElseThrow(() -> new NotFoundException("Imóvel não encontrado."));

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

        UUID principalPessoaId = null;
        for (ParteTransferencia adquirente : adquirentes) {
            imovelProprietarioRepository.salvar(new ImovelProprietario(
                UUID.randomUUID(),
                tenantId,
                imovelId,
                adquirente.contribuinteId(),
                adquirente.porcentagem(),
                adquirente.principal()
            ));

            if (adquirente.principal()) {
                principalPessoaId = contribuinteReferenciaRepository.buscarPessoaId(adquirente.contribuinteId())
                    .orElseThrow(() -> new ValidationException("Contribuinte adquirente principal não encontrado."));
            }
        }

        if (principalPessoaId != null) {
            imovelRepository.salvar(atualizarProprietarioLegado(imovel, principalPessoaId));
        }
    }

    private static Imovel atualizarProprietarioLegado(Imovel imovel, UUID novoProprietarioId) {
        return new Imovel(
            imovel.id(),
            imovel.tenantId(),
            imovel.numeroCadastro(),
            imovel.codigoLegado(),
            novoProprietarioId,
            imovel.tipoId(),
            imovel.enderecoId(),
            imovel.areaTerreno(),
            imovel.areaConstruida(),
            imovel.destinacaoId(),
            imovel.tipoEdificacaoId(),
            imovel.tipoLimitacaoId(),
            imovel.zonaFiscalId(),
            imovel.valorVenalTerreno(),
            imovel.valorVenalConstrucao(),
            imovel.situacao(),
            imovel.anoExercicio(),
            imovel.dataInclusao(),
            imovel.areaTotal(),
            imovel.frente(),
            imovel.fundos(),
            imovel.ladoEsquerdo(),
            imovel.ladoDireito(),
            imovel.quadra(),
            imovel.lote(),
            imovel.loteamento(),
            imovel.edificio(),
            imovel.bloco(),
            imovel.sala(),
            imovel.apartamento(),
            imovel.bairroIptuId(),
            imovel.logradouroIptuId(),
            imovel.valorVenalUnidade(),
            imovel.valorAvaliacao(),
            imovel.enderecoCorrespondenciaId(),
            imovel.observacao()
        );
    }
}
