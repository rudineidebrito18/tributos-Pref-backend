package br.com.tributos.itbi.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.tributos.cadastro.application.AnexarDocumentoInstitucionalService;
import br.com.tributos.cadastro.application.BaixarDocumentoInstitucionalService;
import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.itbi.domain.TipoCalculoGuiaItbi;
import br.com.tributos.itbi.domain.TipoCalculoGuiaItbiRepository;
import br.com.tributos.itbi.domain.TipoGuiaItbi;
import br.com.tributos.itbi.domain.TipoGuiaItbiRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarCatalogoItbiService {

    private final TipoCalculoGuiaItbiRepository tipoCalculoRepository;
    private final TipoGuiaItbiRepository tipoGuiaRepository;
    private final AnexarDocumentoInstitucionalService anexarDocumentoInstitucionalService;
    private final BaixarDocumentoInstitucionalService baixarDocumentoInstitucionalService;

    public GerenciarCatalogoItbiService(
        TipoCalculoGuiaItbiRepository tipoCalculoRepository,
        TipoGuiaItbiRepository tipoGuiaRepository,
        AnexarDocumentoInstitucionalService anexarDocumentoInstitucionalService,
        BaixarDocumentoInstitucionalService baixarDocumentoInstitucionalService
    ) {
        this.tipoCalculoRepository = tipoCalculoRepository;
        this.tipoGuiaRepository = tipoGuiaRepository;
        this.anexarDocumentoInstitucionalService = anexarDocumentoInstitucionalService;
        this.baixarDocumentoInstitucionalService = baixarDocumentoInstitucionalService;
    }

    @Transactional(readOnly = true)
    public List<TipoCalculoGuiaItbi> listarTiposCalculo() {
        return tipoCalculoRepository.listar();
    }

    @Transactional(readOnly = true)
    public TipoCalculoGuiaItbi buscarTipoCalculo(UUID id) {
        return tipoCalculoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Tipo de cálculo não encontrado."));
    }

    @Transactional
    public TipoCalculoGuiaItbi criarTipoCalculo(String descricao) {
        validarDescricao(descricao);
        String descricaoNormalizada = descricao.trim();
        if (tipoCalculoRepository.existePorDescricao(descricaoNormalizada, null)) {
            throw new ValidationException("Já existe um tipo de cálculo com esta descrição.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        return tipoCalculoRepository.salvar(
            new TipoCalculoGuiaItbi(UUID.randomUUID(), tenantId, descricaoNormalizada)
        );
    }

    @Transactional
    public TipoCalculoGuiaItbi atualizarTipoCalculo(UUID id, String descricao) {
        validarDescricao(descricao);
        TipoCalculoGuiaItbi existente = buscarTipoCalculo(id);
        String descricaoNormalizada = descricao.trim();
        if (tipoCalculoRepository.existePorDescricao(descricaoNormalizada, id)) {
            throw new ValidationException("Já existe um tipo de cálculo com esta descrição.");
        }
        return tipoCalculoRepository.salvar(
            new TipoCalculoGuiaItbi(existente.id(), existente.tenantId(), descricaoNormalizada)
        );
    }

    @Transactional
    public void excluirTipoCalculo(UUID id) {
        if (tipoCalculoRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Tipo de cálculo não encontrado.");
        }
        tipoCalculoRepository.excluir(id);
    }

    @Transactional(readOnly = true)
    public List<TipoGuiaItbi> listarTiposGuia() {
        return tipoGuiaRepository.listar();
    }

    @Transactional(readOnly = true)
    public TipoGuiaItbi buscarTipoGuia(UUID id) {
        return tipoGuiaRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Tipo de guia não encontrado."));
    }

    @Transactional
    public TipoGuiaItbi criarTipoGuia(
        String nome,
        BigDecimal aliquota,
        boolean ativo,
        UUID tipoCalculoId,
        boolean permiteDesconto,
        boolean habilitaCalculoValor,
        BigDecimal valor,
        BigDecimal valorParcela,
        String secretaria,
        String cargo
    ) {
        validarTipoGuia(nome, aliquota, tipoCalculoId, valor);
        String nomeNormalizado = nome.trim();
        if (tipoGuiaRepository.existePorNome(nomeNormalizado, null)) {
            throw new ValidationException("Já existe um tipo de guia com este nome.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        return tipoGuiaRepository.salvar(new TipoGuiaItbi(
            UUID.randomUUID(), tenantId, nomeNormalizado, aliquota, ativo,
            tipoCalculoId, permiteDesconto, habilitaCalculoValor, valor,
            valorParcela, secretaria, cargo, null
        ));
    }

    @Transactional
    public TipoGuiaItbi atualizarTipoGuia(
        UUID id,
        String nome,
        BigDecimal aliquota,
        boolean ativo,
        UUID tipoCalculoId,
        boolean permiteDesconto,
        boolean habilitaCalculoValor,
        BigDecimal valor,
        BigDecimal valorParcela,
        String secretaria,
        String cargo
    ) {
        validarTipoGuia(nome, aliquota, tipoCalculoId, valor);
        TipoGuiaItbi existente = buscarTipoGuia(id);
        String nomeNormalizado = nome.trim();
        if (tipoGuiaRepository.existePorNome(nomeNormalizado, id)) {
            throw new ValidationException("Já existe um tipo de guia com este nome.");
        }
        return tipoGuiaRepository.salvar(new TipoGuiaItbi(
            existente.id(), existente.tenantId(), nomeNormalizado, aliquota, ativo,
            tipoCalculoId, permiteDesconto, habilitaCalculoValor, valor,
            valorParcela, secretaria, cargo, existente.assinaturaDocumentoId()
        ));
    }

    @Transactional
    public void excluirTipoGuia(UUID id) {
        if (tipoGuiaRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Tipo de guia não encontrado.");
        }
        tipoGuiaRepository.excluir(id);
    }

    @Transactional
    public TipoGuiaItbi anexarAssinaturaTipoGuia(UUID id, MultipartFile arquivo) {
        TipoGuiaItbi existente = buscarTipoGuia(id);
        Documento documento = anexarDocumentoInstitucionalService.executar("ASSINATURA_TIPO_GUIA_ITBI", arquivo);
        return tipoGuiaRepository.salvar(new TipoGuiaItbi(
            existente.id(), existente.tenantId(), existente.nome(), existente.aliquota(), existente.ativo(),
            existente.tipoCalculoId(), existente.permiteDesconto(), existente.habilitaCalculoValor(),
            existente.valor(), existente.valorParcela(), existente.secretaria(), existente.cargo(),
            documento.id()
        ));
    }

    @Transactional(readOnly = true)
    public BaixarDocumentoInstitucionalService.ArquivoParaDownload baixarAssinaturaTipoGuia(UUID id) {
        TipoGuiaItbi tipoGuia = buscarTipoGuia(id);
        if (tipoGuia.assinaturaDocumentoId() == null) {
            throw new NotFoundException("Assinatura não encontrada.");
        }
        return baixarDocumentoInstitucionalService.executar(tipoGuia.assinaturaDocumentoId());
    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new ValidationException("Informe a descrição do tipo de cálculo.");
        }
    }

    private void validarTipoGuia(String nome, BigDecimal aliquota, UUID tipoCalculoId, BigDecimal valor) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe a descrição do tipo de guia.");
        }
        if (aliquota == null) {
            throw new ValidationException("Informe a alíquota do tipo de guia.");
        }
        if (tipoCalculoId == null) {
            throw new ValidationException("Informe o tipo de cálculo.");
        }
        tipoCalculoRepository.buscarPorId(tipoCalculoId)
            .orElseThrow(() -> new ValidationException("Tipo de cálculo inválido."));
        if (valor == null) {
            throw new ValidationException("Informe o valor do tipo de guia.");
        }
    }
}
