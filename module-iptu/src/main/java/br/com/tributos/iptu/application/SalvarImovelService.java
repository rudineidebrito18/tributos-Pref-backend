package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.CatalogoIptu;
import br.com.tributos.iptu.domain.CatalogoIptuRepository;
import br.com.tributos.iptu.domain.EnderecoReferenciaRepository;
import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.PessoaReferenciaRepository;
import br.com.tributos.iptu.domain.SituacaoImovel;
import br.com.tributos.iptu.domain.TipoCatalogoIptu;
import br.com.tributos.iptu.domain.TipoImovelNomes;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class SalvarImovelService {

    private final ImovelRepository imovelRepository;
    private final PessoaReferenciaRepository pessoaReferenciaRepository;
    private final EnderecoReferenciaRepository enderecoReferenciaRepository;
    private final CatalogoIptuRepository catalogoIptuRepository;

    public SalvarImovelService(
        ImovelRepository imovelRepository,
        PessoaReferenciaRepository pessoaReferenciaRepository,
        EnderecoReferenciaRepository enderecoReferenciaRepository,
        CatalogoIptuRepository catalogoIptuRepository
    ) {
        this.imovelRepository = imovelRepository;
        this.pessoaReferenciaRepository = pessoaReferenciaRepository;
        this.enderecoReferenciaRepository = enderecoReferenciaRepository;
        this.catalogoIptuRepository = catalogoIptuRepository;
    }

    @Transactional
    public Imovel executar(SalvarImovelComando comando, UUID idExistente) {
        if (!pessoaReferenciaRepository.existe(comando.proprietarioId())) {
            throw new ValidationException("Pessoa proprietária não encontrada.");
        }

        if (comando.enderecoId() != null && !enderecoReferenciaRepository.existe(comando.enderecoId())) {
            throw new ValidationException("Endereço não encontrado.");
        }

        CatalogoIptu tipo = catalogoIptuRepository.buscarPorId(TipoCatalogoIptu.TIPO_IMOVEL, comando.tipoId())
            .orElseThrow(() -> new ValidationException("Informe um tipo de imóvel válido."));

        validarCatalogoOpcional(TipoCatalogoIptu.DESTINACAO, comando.destinacaoId(), "destinação");
        validarCatalogoOpcional(TipoCatalogoIptu.TIPO_EDIFICACAO, comando.tipoEdificacaoId(), "tipo de edificação");
        validarCatalogoOpcional(TipoCatalogoIptu.TIPO_LIMITACAO, comando.tipoLimitacaoId(), "tipo de limitação");

        validarAreasPorTipo(tipo.nome(), comando.areaTerreno(), comando.areaConstruida());

        UUID tenantId = TenantContext.getObrigatorio();
        UUID id = idExistente != null ? idExistente : UUID.randomUUID();
        long numeroCadastro;
        String codigoLegado = normalizarCodigoLegado(comando.codigoLegado());

        if (idExistente == null) {
            numeroCadastro = imovelRepository.proximoNumeroCadastro();
        } else {
            Imovel existente = imovelRepository.buscarPorId(idExistente)
                .orElseThrow(() -> new NotFoundException("Imóvel não encontrado."));
            numeroCadastro = existente.numeroCadastro();
            if (codigoLegado != null && imovelRepository.buscarPorCodigoLegado(codigoLegado)
                .filter(i -> !i.id().equals(idExistente))
                .isPresent()) {
                throw new ValidationException("Já existe um imóvel com este código legado.");
            }
        }

        if (idExistente == null && codigoLegado != null && imovelRepository.buscarPorCodigoLegado(codigoLegado).isPresent()) {
            throw new ValidationException("Já existe um imóvel com este código legado.");
        }

        SituacaoImovel situacao = comando.situacao() != null
            ? SituacaoImovel.valueOf(comando.situacao().name())
            : SituacaoImovel.ATIVO;

        BigDecimal valorVenalTerreno = comando.valorVenalTerreno() != null ? comando.valorVenalTerreno() : BigDecimal.ZERO;
        BigDecimal valorVenalConstrucao = comando.valorVenalConstrucao() != null ? comando.valorVenalConstrucao() : BigDecimal.ZERO;

        Imovel imovel = new Imovel(
            id,
            tenantId,
            numeroCadastro,
            codigoLegado,
            comando.proprietarioId(),
            comando.tipoId(),
            comando.enderecoId(),
            comando.areaTerreno(),
            comando.areaConstruida(),
            comando.destinacaoId(),
            comando.tipoEdificacaoId(),
            comando.tipoLimitacaoId(),
            valorVenalTerreno,
            valorVenalConstrucao,
            situacao
        );

        return imovelRepository.salvar(imovel);
    }

    private void validarCatalogoOpcional(TipoCatalogoIptu tipo, UUID id, String rotulo) {
        if (id != null && catalogoIptuRepository.buscarPorId(tipo, id).isEmpty()) {
            throw new ValidationException("Informe um(a) " + rotulo + " válido(a).");
        }
    }

    private static void validarAreasPorTipo(String nomeTipo, BigDecimal areaTerreno, BigDecimal areaConstruida) {
        if (TipoImovelNomes.PREDIAL.equalsIgnoreCase(nomeTipo)) {
            if (areaConstruida == null || areaConstruida.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Imóvel predial exige área construída maior que zero.");
            }
        }
        if (TipoImovelNomes.TERRITORIAL.equalsIgnoreCase(nomeTipo)) {
            if (areaTerreno == null || areaTerreno.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Imóvel territorial exige área de terreno maior que zero.");
            }
        }
    }

    private static String normalizarCodigoLegado(String codigoLegado) {
        if (codigoLegado == null || codigoLegado.isBlank()) {
            return null;
        }
        return codigoLegado.trim();
    }
}
