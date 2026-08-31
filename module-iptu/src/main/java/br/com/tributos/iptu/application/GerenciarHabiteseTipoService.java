package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.tributos.cadastro.application.AnexarDocumentoInstitucionalService;
import br.com.tributos.cadastro.application.BaixarDocumentoInstitucionalService;
import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.iptu.domain.ImovelHabiteseTipo;
import br.com.tributos.iptu.domain.ImovelHabiteseTipoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarHabiteseTipoService {

    private final ImovelHabiteseTipoRepository habiteseTipoRepository;
    private final AnexarDocumentoInstitucionalService anexarDocumentoInstitucionalService;
    private final BaixarDocumentoInstitucionalService baixarDocumentoInstitucionalService;

    public GerenciarHabiteseTipoService(
        ImovelHabiteseTipoRepository habiteseTipoRepository,
        AnexarDocumentoInstitucionalService anexarDocumentoInstitucionalService,
        BaixarDocumentoInstitucionalService baixarDocumentoInstitucionalService
    ) {
        this.habiteseTipoRepository = habiteseTipoRepository;
        this.anexarDocumentoInstitucionalService = anexarDocumentoInstitucionalService;
        this.baixarDocumentoInstitucionalService = baixarDocumentoInstitucionalService;
    }

    @Transactional(readOnly = true)
    public List<ImovelHabiteseTipo> listar() {
        return habiteseTipoRepository.listar();
    }

    @Transactional(readOnly = true)
    public ImovelHabiteseTipo buscar(UUID id) {
        return habiteseTipoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Tipo de habite-se não encontrado."));
    }

    @Transactional
    public ImovelHabiteseTipo criar(
        String nome,
        boolean ativo,
        String titulo,
        boolean permiteDesconto,
        boolean habilitaCalculoValor,
        BigDecimal valor,
        String secretaria,
        String cargo
    ) {
        validarCampos(nome, titulo, valor);
        String nomeNormalizado = nome.trim();
        if (habiteseTipoRepository.existePorNome(nomeNormalizado, null)) {
            throw new ValidationException("Já existe um tipo de habite-se com este nome.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        ImovelHabiteseTipo tipo = new ImovelHabiteseTipo(
            UUID.randomUUID(), tenantId, nomeNormalizado, ativo, titulo.trim(),
            permiteDesconto, habilitaCalculoValor, valor, secretaria, cargo, null, null
        );
        return habiteseTipoRepository.salvar(tipo);
    }

    @Transactional
    public ImovelHabiteseTipo atualizar(
        UUID id,
        String nome,
        boolean ativo,
        String titulo,
        boolean permiteDesconto,
        boolean habilitaCalculoValor,
        BigDecimal valor,
        String secretaria,
        String cargo
    ) {
        validarCampos(nome, titulo, valor);
        ImovelHabiteseTipo existente = buscar(id);
        String nomeNormalizado = nome.trim();
        if (habiteseTipoRepository.existePorNome(nomeNormalizado, id)) {
            throw new ValidationException("Já existe um tipo de habite-se com este nome.");
        }
        ImovelHabiteseTipo atualizado = new ImovelHabiteseTipo(
            existente.id(), existente.tenantId(), nomeNormalizado, ativo, titulo.trim(),
            permiteDesconto, habilitaCalculoValor, valor, secretaria, cargo,
            existente.assinaturaDocumentoId(), existente.criadoEm()
        );
        return habiteseTipoRepository.salvar(atualizado);
    }

    @Transactional
    public void excluir(UUID id) {
        if (habiteseTipoRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Tipo de habite-se não encontrado.");
        }
        habiteseTipoRepository.excluir(id);
    }

    @Transactional
    public ImovelHabiteseTipo anexarAssinatura(UUID id, MultipartFile arquivo) {
        ImovelHabiteseTipo existente = buscar(id);
        Documento documento = anexarDocumentoInstitucionalService.executar("ASSINATURA_HABITESE", arquivo);
        ImovelHabiteseTipo atualizado = new ImovelHabiteseTipo(
            existente.id(), existente.tenantId(), existente.nome(), existente.ativo(),
            existente.titulo(), existente.permiteDesconto(), existente.habilitaCalculoValor(),
            existente.valor(), existente.secretaria(), existente.cargo(),
            documento.id(), existente.criadoEm()
        );
        return habiteseTipoRepository.salvar(atualizado);
    }

    @Transactional(readOnly = true)
    public BaixarDocumentoInstitucionalService.ArquivoParaDownload baixarAssinatura(UUID id) {
        ImovelHabiteseTipo tipo = buscar(id);
        if (tipo.assinaturaDocumentoId() == null) {
            throw new NotFoundException("Assinatura não encontrada.");
        }
        return baixarDocumentoInstitucionalService.executar(tipo.assinaturaDocumentoId());
    }

    private static void validarCampos(String nome, String titulo, BigDecimal valor) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe a descrição do tipo de habite-se.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new ValidationException("Informe o título do tipo de habite-se.");
        }
        if (valor == null) {
            throw new ValidationException("Informe o valor do tipo de habite-se.");
        }
    }
}
