package br.com.tributos.iss.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.GrupoServico;
import br.com.tributos.iss.domain.GrupoServicoRepository;
import br.com.tributos.iss.domain.LocalIncidencia;
import br.com.tributos.iss.domain.LocalIncidenciaRepository;
import br.com.tributos.iss.domain.SituacaoCnd;
import br.com.tributos.iss.domain.SituacaoCndRepository;
import br.com.tributos.iss.domain.StatusSolicitacao;
import br.com.tributos.iss.domain.StatusSolicitacaoRepository;
import br.com.tributos.iss.domain.TipoSolicitacao;
import br.com.tributos.iss.domain.TipoSolicitacaoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarApoioIssService {

    private final SituacaoCndRepository situacaoCndRepository;
    private final TipoSolicitacaoRepository tipoSolicitacaoRepository;
    private final StatusSolicitacaoRepository statusSolicitacaoRepository;
    private final LocalIncidenciaRepository localIncidenciaRepository;
    private final GrupoServicoRepository grupoServicoRepository;

    public GerenciarApoioIssService(
        SituacaoCndRepository situacaoCndRepository,
        TipoSolicitacaoRepository tipoSolicitacaoRepository,
        StatusSolicitacaoRepository statusSolicitacaoRepository,
        LocalIncidenciaRepository localIncidenciaRepository,
        GrupoServicoRepository grupoServicoRepository
    ) {
        this.situacaoCndRepository = situacaoCndRepository;
        this.tipoSolicitacaoRepository = tipoSolicitacaoRepository;
        this.statusSolicitacaoRepository = statusSolicitacaoRepository;
        this.localIncidenciaRepository = localIncidenciaRepository;
        this.grupoServicoRepository = grupoServicoRepository;
    }

    // --- Situação CND ---

    @Transactional(readOnly = true)
    public List<SituacaoCnd> listarSituacoesCnd() {
        return situacaoCndRepository.listar();
    }

    @Transactional(readOnly = true)
    public SituacaoCnd buscarSituacaoCnd(UUID id) {
        return situacaoCndRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Situação CND não encontrada."));
    }

    @Transactional
    public SituacaoCnd criarSituacaoCnd(String descricao, String titulo, boolean ativo) {
        validarDescricao(descricao);
        validarTitulo(titulo);
        String descricaoNormalizada = descricao.trim();
        if (situacaoCndRepository.existePorDescricao(descricaoNormalizada, null)) {
            throw new ValidationException("Já existe uma situação CND com esta descrição.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        SituacaoCnd situacao = new SituacaoCnd(
            UUID.randomUUID(), tenantId, descricaoNormalizada, titulo.trim(), ativo
        );
        return situacaoCndRepository.salvar(situacao);
    }

    @Transactional
    public SituacaoCnd atualizarSituacaoCnd(UUID id, String descricao, String titulo, boolean ativo) {
        validarDescricao(descricao);
        validarTitulo(titulo);
        SituacaoCnd existente = buscarSituacaoCnd(id);
        String descricaoNormalizada = descricao.trim();
        if (situacaoCndRepository.existePorDescricao(descricaoNormalizada, id)) {
            throw new ValidationException("Já existe uma situação CND com esta descrição.");
        }
        SituacaoCnd atualizada = new SituacaoCnd(
            existente.id(), existente.tenantId(), descricaoNormalizada, titulo.trim(), ativo
        );
        return situacaoCndRepository.salvar(atualizada);
    }

    @Transactional
    public void excluirSituacaoCnd(UUID id) {
        if (situacaoCndRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Situação CND não encontrada.");
        }
        situacaoCndRepository.excluir(id);
    }

    // --- Tipo de Solicitação ---

    @Transactional(readOnly = true)
    public List<TipoSolicitacao> listarTiposSolicitacao() {
        return tipoSolicitacaoRepository.listar();
    }

    @Transactional(readOnly = true)
    public TipoSolicitacao buscarTipoSolicitacao(UUID id) {
        return tipoSolicitacaoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Tipo de solicitação não encontrado."));
    }

    @Transactional
    public TipoSolicitacao criarTipoSolicitacao(String descricao, UUID usuarioNotificarId, boolean ativo) {
        validarDescricao(descricao);
        UUID tenantId = TenantContext.getObrigatorio();
        TipoSolicitacao tipo = new TipoSolicitacao(
            UUID.randomUUID(), tenantId, descricao.trim(), usuarioNotificarId, ativo
        );
        return tipoSolicitacaoRepository.salvar(tipo);
    }

    @Transactional
    public TipoSolicitacao atualizarTipoSolicitacao(
        UUID id, String descricao, UUID usuarioNotificarId, boolean ativo
    ) {
        validarDescricao(descricao);
        TipoSolicitacao existente = buscarTipoSolicitacao(id);
        TipoSolicitacao atualizado = new TipoSolicitacao(
            existente.id(), existente.tenantId(), descricao.trim(), usuarioNotificarId, ativo
        );
        return tipoSolicitacaoRepository.salvar(atualizado);
    }

    @Transactional
    public void excluirTipoSolicitacao(UUID id) {
        if (tipoSolicitacaoRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Tipo de solicitação não encontrado.");
        }
        tipoSolicitacaoRepository.excluir(id);
    }

    // --- Status de Solicitação ---

    @Transactional(readOnly = true)
    public List<StatusSolicitacao> listarStatusSolicitacao() {
        return statusSolicitacaoRepository.listar();
    }

    @Transactional(readOnly = true)
    public StatusSolicitacao buscarStatusSolicitacao(UUID id) {
        return statusSolicitacaoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Status de solicitação não encontrado."));
    }

    @Transactional
    public StatusSolicitacao criarStatusSolicitacao(String descricao, boolean ativo) {
        validarDescricao(descricao);
        UUID tenantId = TenantContext.getObrigatorio();
        StatusSolicitacao status = new StatusSolicitacao(
            UUID.randomUUID(), tenantId, descricao.trim(), ativo
        );
        return statusSolicitacaoRepository.salvar(status);
    }

    @Transactional
    public StatusSolicitacao atualizarStatusSolicitacao(UUID id, String descricao, boolean ativo) {
        validarDescricao(descricao);
        StatusSolicitacao existente = buscarStatusSolicitacao(id);
        StatusSolicitacao atualizado = new StatusSolicitacao(
            existente.id(), existente.tenantId(), descricao.trim(), ativo
        );
        return statusSolicitacaoRepository.salvar(atualizado);
    }

    @Transactional
    public void excluirStatusSolicitacao(UUID id) {
        if (statusSolicitacaoRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Status de solicitação não encontrado.");
        }
        statusSolicitacaoRepository.excluir(id);
    }

    // --- Local de Incidência ---

    @Transactional(readOnly = true)
    public List<LocalIncidencia> listarLocaisIncidencia() {
        return localIncidenciaRepository.listar();
    }

    @Transactional(readOnly = true)
    public LocalIncidencia buscarLocalIncidencia(UUID id) {
        return localIncidenciaRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Local de incidência não encontrado."));
    }

    @Transactional
    public LocalIncidencia criarLocalIncidencia(String descricao, boolean ativo) {
        validarDescricao(descricao);
        String descricaoNormalizada = descricao.trim();
        if (localIncidenciaRepository.existePorDescricao(descricaoNormalizada, null)) {
            throw new ValidationException("Já existe um local de incidência com esta descrição.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        LocalIncidencia local = new LocalIncidencia(
            UUID.randomUUID(), tenantId, descricaoNormalizada, ativo
        );
        return localIncidenciaRepository.salvar(local);
    }

    @Transactional
    public LocalIncidencia atualizarLocalIncidencia(UUID id, String descricao, boolean ativo) {
        validarDescricao(descricao);
        LocalIncidencia existente = buscarLocalIncidencia(id);
        String descricaoNormalizada = descricao.trim();
        if (localIncidenciaRepository.existePorDescricao(descricaoNormalizada, id)) {
            throw new ValidationException("Já existe um local de incidência com esta descrição.");
        }
        LocalIncidencia atualizado = new LocalIncidencia(
            existente.id(), existente.tenantId(), descricaoNormalizada, ativo
        );
        return localIncidenciaRepository.salvar(atualizado);
    }

    @Transactional
    public void excluirLocalIncidencia(UUID id) {
        if (localIncidenciaRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Local de incidência não encontrado.");
        }
        localIncidenciaRepository.excluir(id);
    }

    // --- Grupo de Serviço ---

    @Transactional(readOnly = true)
    public List<GrupoServico> listarGruposServico() {
        return grupoServicoRepository.listar();
    }

    @Transactional(readOnly = true)
    public GrupoServico buscarGrupoServico(UUID id) {
        return grupoServicoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Grupo de serviço não encontrado."));
    }

    @Transactional
    public GrupoServico criarGrupoServico(String codigo, String descricao) {
        validarCodigo(codigo);
        validarDescricao(descricao);
        String codigoNormalizado = codigo.trim();
        if (grupoServicoRepository.existePorCodigo(codigoNormalizado, null)) {
            throw new ValidationException("Já existe um grupo de serviço com este código.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        GrupoServico grupo = new GrupoServico(
            UUID.randomUUID(), tenantId, codigoNormalizado, descricao.trim()
        );
        return grupoServicoRepository.salvar(grupo);
    }

    @Transactional
    public GrupoServico atualizarGrupoServico(UUID id, String codigo, String descricao) {
        validarCodigo(codigo);
        validarDescricao(descricao);
        GrupoServico existente = buscarGrupoServico(id);
        String codigoNormalizado = codigo.trim();
        if (grupoServicoRepository.existePorCodigo(codigoNormalizado, id)) {
            throw new ValidationException("Já existe um grupo de serviço com este código.");
        }
        GrupoServico atualizado = new GrupoServico(
            existente.id(), existente.tenantId(), codigoNormalizado, descricao.trim()
        );
        return grupoServicoRepository.salvar(atualizado);
    }

    @Transactional
    public void excluirGrupoServico(UUID id) {
        if (grupoServicoRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Grupo de serviço não encontrado.");
        }
        grupoServicoRepository.excluir(id);
    }

    private static void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new ValidationException("Informe a descrição.");
        }
    }

    private static void validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new ValidationException("Informe o título.");
        }
    }

    private static void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new ValidationException("Informe o código.");
        }
    }
}
