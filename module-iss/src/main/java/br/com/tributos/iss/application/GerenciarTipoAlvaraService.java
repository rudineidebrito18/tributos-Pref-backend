package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.adapters.out.persistence.UsuarioReferenciaJpaRepository;
import br.com.tributos.iss.domain.TipoAlvara;
import br.com.tributos.iss.domain.TipoAlvaraRepository;
import br.com.tributos.iss.domain.ValorAlvara;
import br.com.tributos.iss.domain.ValorAlvaraRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.identity.UsuarioAutenticadoPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarTipoAlvaraService {

    private final TipoAlvaraRepository tipoAlvaraRepository;
    private final ValorAlvaraRepository valorAlvaraRepository;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;
    private final UsuarioReferenciaJpaRepository usuarioReferenciaJpaRepository;

    public GerenciarTipoAlvaraService(
        TipoAlvaraRepository tipoAlvaraRepository,
        ValorAlvaraRepository valorAlvaraRepository,
        UsuarioAutenticadoPort usuarioAutenticadoPort,
        UsuarioReferenciaJpaRepository usuarioReferenciaJpaRepository
    ) {
        this.tipoAlvaraRepository = tipoAlvaraRepository;
        this.valorAlvaraRepository = valorAlvaraRepository;
        this.usuarioAutenticadoPort = usuarioAutenticadoPort;
        this.usuarioReferenciaJpaRepository = usuarioReferenciaJpaRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoAlvara> listar() {
        return tipoAlvaraRepository.listar();
    }

    @Transactional(readOnly = true)
    public TipoAlvara buscar(UUID id) {
        return tipoAlvaraRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Tipo de alvará não encontrado."));
    }

    @Transactional
    public TipoAlvara criar(SalvarTipoAlvaraComando comando) {
        validarCampos(comando);
        String nomeNormalizado = comando.nome().trim();
        if (tipoAlvaraRepository.existePorNome(nomeNormalizado, null)) {
            throw new ValidationException("Já existe um tipo de alvará com este nome.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        TipoAlvara tipo = paraDominio(UUID.randomUUID(), tenantId, comando);
        return tipoAlvaraRepository.salvar(tipo);
    }

    @Transactional
    public TipoAlvara atualizar(UUID id, SalvarTipoAlvaraComando comando) {
        validarCampos(comando);
        TipoAlvara existente = buscar(id);
        String nomeNormalizado = comando.nome().trim();
        if (tipoAlvaraRepository.existePorNome(nomeNormalizado, id)) {
            throw new ValidationException("Já existe um tipo de alvará com este nome.");
        }

        if (existente.valorBase().compareTo(comando.valorBase()) != 0) {
            registrarHistoricoValor(existente, comando);
        }

        TipoAlvara atualizado = paraDominio(existente.id(), existente.tenantId(), comando);
        return tipoAlvaraRepository.salvar(atualizado);
    }

    @Transactional(readOnly = true)
    public List<HistoricoValorAlvaraItem> listarHistoricoValores(UUID tipoAlvaraId) {
        buscar(tipoAlvaraId);
        return valorAlvaraRepository.listarPorTipoAlvara(tipoAlvaraId).stream()
            .map(this::paraHistoricoItem)
            .toList();
    }

    private HistoricoValorAlvaraItem paraHistoricoItem(ValorAlvara historico) {
        String usuario = historico.usuarioId() != null
            ? usuarioReferenciaJpaRepository.findById(historico.usuarioId()).map(u -> u.getLogin()).orElse("—")
            : "—";
        return new HistoricoValorAlvaraItem(
            historico.atualizadoEm(),
            usuario,
            historico.valor(),
            historico.anoVigencia()
        );
    }

    private void registrarHistoricoValor(TipoAlvara existente, SalvarTipoAlvaraComando comando) {
        short anoVigencia = comando.anoVigencia() != null
            ? comando.anoVigencia()
            : (short) LocalDate.now().getYear();

        ValorAlvara historico = new ValorAlvara(
            UUID.randomUUID(),
            existente.tenantId(),
            existente.id(),
            anoVigencia,
            comando.valorBase(),
            usuarioAutenticadoPort.usuarioIdAtualObrigatorio(),
            Instant.now()
        );
        valorAlvaraRepository.salvar(historico);
    }

    private static TipoAlvara paraDominio(UUID id, UUID tenantId, SalvarTipoAlvaraComando comando) {
        return new TipoAlvara(
            id,
            tenantId,
            comando.nome().trim(),
            comando.valorBase(),
            comando.diasValidade(),
            comando.ativo(),
            comando.anoVigencia(),
            comando.identificacaoModeloDocumento(),
            comando.permiteValorDinamico(),
            comando.permiteCalculoValor(),
            comando.unidadeMedidaDescritivo(),
            comando.habilitarValidade(),
            comando.habilitarCalculoVencimento(),
            comando.baseVencimento(),
            comando.diasMesesVencimento(),
            comando.titulo(),
            comando.secretaria(),
            comando.cargo(),
            comando.assinaturaDocumentoId()
        );
    }

    private static void validarCampos(SalvarTipoAlvaraComando comando) {
        if (comando.nome() == null || comando.nome().isBlank()) {
            throw new ValidationException("Informe o nome do tipo de alvará.");
        }
        if (comando.valorBase() == null || comando.valorBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Informe um valor base válido para o tipo de alvará.");
        }
        if (comando.diasValidade() <= 0) {
            throw new ValidationException("Os dias de validade devem ser maiores que zero.");
        }
    }

    public record HistoricoValorAlvaraItem(
        Instant dataHoraAtualizacao,
        String usuario,
        BigDecimal valor,
        short anoVigencia
    ) {
    }
}
