package br.com.tributos.iptu.application;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.CatalogoIptuRepository;
import br.com.tributos.iptu.domain.HabiteseImovel;
import br.com.tributos.iptu.domain.HabiteseImovelRepository;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.TipoCatalogoIptu;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class EmitirHabiteseService {

    private final HabiteseImovelRepository habiteseImovelRepository;
    private final ImovelRepository imovelRepository;
    private final CatalogoIptuRepository catalogoIptuRepository;

    public EmitirHabiteseService(
        HabiteseImovelRepository habiteseImovelRepository,
        ImovelRepository imovelRepository,
        CatalogoIptuRepository catalogoIptuRepository
    ) {
        this.habiteseImovelRepository = habiteseImovelRepository;
        this.imovelRepository = imovelRepository;
        this.catalogoIptuRepository = catalogoIptuRepository;
    }

    @Transactional
    public HabiteseImovel executar(UUID imovelId, UUID tipoId, LocalDate dataEmissao) {
        if (!imovelRepository.buscarPorId(imovelId).isPresent()) {
            throw new NotFoundException("Imóvel não encontrado.");
        }

        var tipo = catalogoIptuRepository.buscarPorId(TipoCatalogoIptu.HABITESE_TIPO, tipoId)
            .orElseThrow(() -> new ValidationException("Informe um tipo de habite-se válido."));

        if (!tipo.ativo()) {
            throw new ValidationException("O tipo de habite-se selecionado está inativo.");
        }

        if (dataEmissao == null) {
            throw new ValidationException("Informe a data de emissão do habite-se.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        long numero = habiteseImovelRepository.proximoNumero();
        Instant dataEmissaoTs = Instant.now();

        HabiteseImovel habitese = new HabiteseImovel(
            UUID.randomUUID(),
            tenantId,
            imovelId,
            tipoId,
            numero,
            dataEmissao,
            dataEmissaoTs
        );

        return habiteseImovelRepository.salvar(habitese);
    }
}
