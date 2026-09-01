package br.com.tributos.iptu.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.ContribuinteReferenciaRepository;
import br.com.tributos.iptu.domain.ImovelProprietario;
import br.com.tributos.iptu.domain.ImovelProprietarioRepository;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.kernel.audit.AuditoriaPort;
import br.com.tributos.kernel.audit.RegistroAuditoria;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarImovelProprietarioService {

    private final ImovelRepository imovelRepository;
    private final ImovelProprietarioRepository imovelProprietarioRepository;
    private final ContribuinteReferenciaRepository contribuinteReferenciaRepository;
    private final AuditoriaPort auditoriaPort;

    public GerenciarImovelProprietarioService(
        ImovelRepository imovelRepository,
        ImovelProprietarioRepository imovelProprietarioRepository,
        ContribuinteReferenciaRepository contribuinteReferenciaRepository,
        AuditoriaPort auditoriaPort
    ) {
        this.imovelRepository = imovelRepository;
        this.imovelProprietarioRepository = imovelProprietarioRepository;
        this.contribuinteReferenciaRepository = contribuinteReferenciaRepository;
        this.auditoriaPort = auditoriaPort;
    }

    @Transactional(readOnly = true)
    public List<ImovelProprietario> listar(UUID imovelId) {
        validarImovelExiste(imovelId);
        return imovelProprietarioRepository.listarPorImovel(imovelId);
    }

    @Transactional
    public ImovelProprietario adicionar(UUID imovelId, AdicionarImovelProprietarioComando comando) {
        validarImovelExiste(imovelId);

        if (comando.porcentagem() == null) {
            throw new ValidationException("Informe a porcentagem do proprietário.");
        }

        if (!contribuinteReferenciaRepository.existe(comando.contribuinteId())) {
            throw new ValidationException("Contribuinte não encontrado.");
        }

        if (imovelProprietarioRepository.existePorImovelEContribuinte(imovelId, comando.contribuinteId(), null)) {
            throw new ValidationException("Este contribuinte já é proprietário do imóvel.");
        }

        List<ImovelProprietario> atuais = new ArrayList<>(imovelProprietarioRepository.listarPorImovel(imovelId));
        UUID tenantId = TenantContext.getObrigatorio();
        ImovelProprietario novo = new ImovelProprietario(
            UUID.randomUUID(),
            tenantId,
            imovelId,
            comando.contribuinteId(),
            comando.porcentagem(),
            comando.proprietarioPrincipal()
        );
        atuais.add(novo);
        ImovelProprietario.validarComposicaoParcial(atuais);

        ImovelProprietario salvo = imovelProprietarioRepository.salvar(novo);

        auditoriaPort.registrar(new RegistroAuditoria(
            "imovel_proprietario",
            salvo.id().toString(),
            "ADICIONAR",
            null,
            Map.of(
                "imovelId", imovelId.toString(),
                "contribuinteId", comando.contribuinteId().toString(),
                "porcentagem", comando.porcentagem(),
                "proprietarioPrincipal", comando.proprietarioPrincipal()
            )
        ));

        return salvo;
    }

    @Transactional
    public void remover(UUID imovelId, UUID proprietarioId) {
        validarImovelExiste(imovelId);

        ImovelProprietario existente = imovelProprietarioRepository.buscarPorId(proprietarioId)
            .filter(p -> p.imovelId().equals(imovelId))
            .orElseThrow(() -> new NotFoundException("Proprietário não encontrado para este imóvel."));

        imovelProprietarioRepository.remover(proprietarioId);

        auditoriaPort.registrar(new RegistroAuditoria(
            "imovel_proprietario",
            proprietarioId.toString(),
            "REMOVER",
            Map.of(
                "imovelId", imovelId.toString(),
                "contribuinteId", existente.contribuinteId().toString(),
                "porcentagem", existente.porcentagem(),
                "proprietarioPrincipal", existente.proprietarioPrincipal()
            ),
            null
        ));
    }

    private void validarImovelExiste(UUID imovelId) {
        if (imovelRepository.buscarPorId(imovelId).isEmpty()) {
            throw new NotFoundException("Imóvel não encontrado.");
        }
    }
}
