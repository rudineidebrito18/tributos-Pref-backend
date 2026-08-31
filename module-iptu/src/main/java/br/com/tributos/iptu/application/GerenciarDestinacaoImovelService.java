package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.ImovelDestinacao;
import br.com.tributos.iptu.domain.ImovelDestinacaoRepository;
import br.com.tributos.iptu.domain.TipoCatalogoIptu;
import br.com.tributos.iptu.domain.CatalogoIptuRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarDestinacaoImovelService {

    private final ImovelDestinacaoRepository destinacaoRepository;
    private final CatalogoIptuRepository catalogoIptuRepository;

    public GerenciarDestinacaoImovelService(
        ImovelDestinacaoRepository destinacaoRepository,
        CatalogoIptuRepository catalogoIptuRepository
    ) {
        this.destinacaoRepository = destinacaoRepository;
        this.catalogoIptuRepository = catalogoIptuRepository;
    }

    @Transactional(readOnly = true)
    public List<ImovelDestinacao> listar() {
        return destinacaoRepository.listar();
    }

    @Transactional(readOnly = true)
    public ImovelDestinacao buscar(UUID id) {
        return destinacaoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Destinação não encontrada."));
    }

    @Transactional
    public ImovelDestinacao criar(String nome, boolean ativo, UUID tipoImovelId, BigDecimal aliquotaIptu) {
        validarCampos(nome, tipoImovelId, aliquotaIptu);
        String nomeNormalizado = nome.trim();
        if (destinacaoRepository.existePorNome(nomeNormalizado, null)) {
            throw new ValidationException("Já existe uma destinação com este nome.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        ImovelDestinacao destinacao = new ImovelDestinacao(
            UUID.randomUUID(), tenantId, nomeNormalizado, ativo, tipoImovelId, aliquotaIptu, null
        );
        return destinacaoRepository.salvar(destinacao);
    }

    @Transactional
    public ImovelDestinacao atualizar(UUID id, String nome, boolean ativo, UUID tipoImovelId, BigDecimal aliquotaIptu) {
        validarCampos(nome, tipoImovelId, aliquotaIptu);
        ImovelDestinacao existente = buscar(id);
        String nomeNormalizado = nome.trim();
        if (destinacaoRepository.existePorNome(nomeNormalizado, id)) {
            throw new ValidationException("Já existe uma destinação com este nome.");
        }
        ImovelDestinacao atualizada = new ImovelDestinacao(
            existente.id(), existente.tenantId(), nomeNormalizado, ativo,
            tipoImovelId, aliquotaIptu, existente.criadoEm()
        );
        return destinacaoRepository.salvar(atualizada);
    }

    @Transactional
    public void excluir(UUID id) {
        if (destinacaoRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Destinação não encontrada.");
        }
        destinacaoRepository.excluir(id);
    }

    private void validarCampos(String nome, UUID tipoImovelId, BigDecimal aliquotaIptu) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe a descrição da destinação.");
        }
        if (tipoImovelId == null) {
            throw new ValidationException("Informe o tipo de imóvel.");
        }
        catalogoIptuRepository.buscarPorId(TipoCatalogoIptu.TIPO_IMOVEL, tipoImovelId)
            .orElseThrow(() -> new ValidationException("Tipo de imóvel inválido."));
        if (aliquotaIptu == null) {
            throw new ValidationException("Informe a alíquota de IPTU.");
        }
        if (aliquotaIptu.compareTo(BigDecimal.ZERO) < 0 || aliquotaIptu.compareTo(new BigDecimal("100")) > 0) {
            throw new ValidationException("A alíquota de IPTU deve estar entre 0 e 100.");
        }
    }
}
