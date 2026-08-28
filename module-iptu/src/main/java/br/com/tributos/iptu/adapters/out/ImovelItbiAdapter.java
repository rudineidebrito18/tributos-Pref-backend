package br.com.tributos.iptu.adapters.out;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.application.CalculadorIptu;
import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.PessoaReferenciaRepository;
import br.com.tributos.iptu.domain.SituacaoImovel;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.iptu.ImovelItbiPort;

@Component
public class ImovelItbiAdapter implements ImovelItbiPort {

    private final ImovelRepository imovelRepository;
    private final PessoaReferenciaRepository pessoaReferenciaRepository;

    public ImovelItbiAdapter(ImovelRepository imovelRepository, PessoaReferenciaRepository pessoaReferenciaRepository) {
        this.imovelRepository = imovelRepository;
        this.pessoaReferenciaRepository = pessoaReferenciaRepository;
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

        Imovel atualizado = new Imovel(
            imovel.id(), imovel.tenantId(), imovel.numeroCadastro(), imovel.codigoLegado(),
            novoProprietarioId, imovel.tipoId(), imovel.enderecoId(), imovel.areaTerreno(),
            imovel.areaConstruida(), imovel.destinacaoId(), imovel.tipoEdificacaoId(),
            imovel.tipoLimitacaoId(), imovel.zonaFiscalId(), imovel.valorVenalTerreno(),
            imovel.valorVenalConstrucao(), imovel.situacao()
        );
        imovelRepository.salvar(atualizado);
    }
}
