package br.com.tributos.iptu.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.adapters.out.persistence.IptuUsuarioReferenciaJpaRepository;
import br.com.tributos.iptu.domain.ImovelObservacao;
import br.com.tributos.iptu.domain.ImovelObservacaoRepository;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.identity.UsuarioAutenticadoPort;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarImovelObservacaoService {

    private final ImovelRepository imovelRepository;
    private final ImovelObservacaoRepository imovelObservacaoRepository;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;
    private final IptuUsuarioReferenciaJpaRepository usuarioReferenciaJpaRepository;

    public GerenciarImovelObservacaoService(
        ImovelRepository imovelRepository,
        ImovelObservacaoRepository imovelObservacaoRepository,
        UsuarioAutenticadoPort usuarioAutenticadoPort,
        IptuUsuarioReferenciaJpaRepository usuarioReferenciaJpaRepository
    ) {
        this.imovelRepository = imovelRepository;
        this.imovelObservacaoRepository = imovelObservacaoRepository;
        this.usuarioAutenticadoPort = usuarioAutenticadoPort;
        this.usuarioReferenciaJpaRepository = usuarioReferenciaJpaRepository;
    }

    @Transactional(readOnly = true)
    public List<ImovelObservacaoComUsuario> listar(UUID imovelId) {
        validarImovelExiste(imovelId);
        return imovelObservacaoRepository.listarPorImovel(imovelId).stream()
            .map(this::comUsuario)
            .toList();
    }

    @Transactional
    public ImovelObservacaoComUsuario criar(UUID imovelId, String texto) {
        validarImovelExiste(imovelId);

        if (texto == null || texto.isBlank()) {
            throw new ValidationException("Informe o texto da observação.");
        }

        UUID usuarioId = usuarioAutenticadoPort.usuarioIdAtualObrigatorio();
        UUID tenantId = TenantContext.getObrigatorio();

        ImovelObservacao observacao = new ImovelObservacao(
            UUID.randomUUID(),
            tenantId,
            imovelId,
            usuarioId,
            texto.trim(),
            null
        );

        return comUsuario(imovelObservacaoRepository.salvar(observacao));
    }

    private ImovelObservacaoComUsuario comUsuario(ImovelObservacao observacao) {
        String usuario = usuarioReferenciaJpaRepository.findById(observacao.usuarioId())
            .map(u -> u.getLogin())
            .orElse("—");
        return new ImovelObservacaoComUsuario(observacao, usuario);
    }

    private void validarImovelExiste(UUID imovelId) {
        if (imovelRepository.buscarPorId(imovelId).isEmpty()) {
            throw new NotFoundException("Imóvel não encontrado.");
        }
    }
}
