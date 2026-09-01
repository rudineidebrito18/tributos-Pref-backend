package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.ContribuinteReferenciaRepository;
import br.com.tributos.iptu.domain.ImovelProprietario;
import br.com.tributos.iptu.domain.ImovelProprietarioRepository;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class ProprietarioPrincipalImovelService {

    private final ImovelProprietarioRepository imovelProprietarioRepository;
    private final ContribuinteReferenciaRepository contribuinteReferenciaRepository;

    public ProprietarioPrincipalImovelService(
        ImovelProprietarioRepository imovelProprietarioRepository,
        ContribuinteReferenciaRepository contribuinteReferenciaRepository
    ) {
        this.imovelProprietarioRepository = imovelProprietarioRepository;
        this.contribuinteReferenciaRepository = contribuinteReferenciaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UUID> buscarPessoaIdPrincipal(UUID imovelId) {
        return buscarPrincipal(imovelId)
            .flatMap(p -> contribuinteReferenciaRepository.buscarPessoaId(p.contribuinteId()));
    }

    @Transactional
    public void sincronizarProprietarioInicial(UUID imovelId, UUID pessoaId) {
        if (pessoaId == null) {
            return;
        }

        UUID contribuinteId = contribuinteReferenciaRepository.buscarContribuinteIdPorPessoaId(pessoaId)
            .orElseThrow(() -> new ValidationException(
                "Cadastre o proprietário como contribuinte ISS antes de vinculá-lo ao imóvel."
            ));

        List<ImovelProprietario> atuais = imovelProprietarioRepository.listarPorImovel(imovelId);
        if (!atuais.isEmpty()) {
            return;
        }

        UUID tenantId = TenantContext.getObrigatorio();
        imovelProprietarioRepository.salvar(new ImovelProprietario(
            UUID.randomUUID(),
            tenantId,
            imovelId,
            contribuinteId,
            new BigDecimal("100"),
            true
        ));
    }

    private Optional<ImovelProprietario> buscarPrincipal(UUID imovelId) {
        return imovelProprietarioRepository.buscarPrincipalPorImovel(imovelId)
            .or(() -> imovelProprietarioRepository.listarPorImovel(imovelId).stream().findFirst());
    }
}
