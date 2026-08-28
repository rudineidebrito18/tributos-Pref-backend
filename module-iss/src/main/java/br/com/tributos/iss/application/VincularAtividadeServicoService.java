package br.com.tributos.iss.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.AtividadeRepository;
import br.com.tributos.iss.domain.ContribuinteAtividadeServico;
import br.com.tributos.iss.domain.ContribuinteAtividadeServicoRepository;
import br.com.tributos.iss.domain.ContribuinteRepository;
import br.com.tributos.iss.domain.ServicoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class VincularAtividadeServicoService {

    private final ContribuinteAtividadeServicoRepository vinculoRepository;
    private final ContribuinteRepository contribuinteRepository;
    private final AtividadeRepository atividadeRepository;
    private final ServicoRepository servicoRepository;

    public VincularAtividadeServicoService(
        ContribuinteAtividadeServicoRepository vinculoRepository,
        ContribuinteRepository contribuinteRepository,
        AtividadeRepository atividadeRepository,
        ServicoRepository servicoRepository
    ) {
        this.vinculoRepository = vinculoRepository;
        this.contribuinteRepository = contribuinteRepository;
        this.atividadeRepository = atividadeRepository;
        this.servicoRepository = servicoRepository;
    }

    @Transactional(readOnly = true)
    public List<ContribuinteAtividadeServico> listar(UUID contribuinteId) {
        validarContribuinteExiste(contribuinteId);
        return vinculoRepository.listarPorContribuinte(contribuinteId);
    }

    @Transactional
    public ContribuinteAtividadeServico vincular(
        UUID contribuinteId,
        UUID atividadeId,
        UUID servicoId,
        boolean tributavel
    ) {
        validarContribuinteExiste(contribuinteId);
        if (atividadeRepository.buscarPorId(atividadeId).isEmpty()) {
            throw new NotFoundException("Atividade não encontrada.");
        }
        if (servicoRepository.buscarPorId(servicoId).isEmpty()) {
            throw new NotFoundException("Serviço não encontrado.");
        }
        if (vinculoRepository.existeVinculo(contribuinteId, atividadeId, servicoId)) {
            throw new ValidationException("Este vínculo de atividade e serviço já existe para o contribuinte.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        ContribuinteAtividadeServico vinculo = new ContribuinteAtividadeServico(
            UUID.randomUUID(), tenantId, contribuinteId, atividadeId, servicoId, tributavel
        );
        return vinculoRepository.salvar(vinculo);
    }

    @Transactional
    public void desvincular(UUID contribuinteId, UUID vinculoId) {
        validarContribuinteExiste(contribuinteId);
        ContribuinteAtividadeServico vinculo = vinculoRepository.buscarPorId(vinculoId)
            .orElseThrow(() -> new NotFoundException("Vínculo não encontrado."));
        if (!vinculo.contribuinteId().equals(contribuinteId)) {
            throw new NotFoundException("Vínculo não encontrado para este contribuinte.");
        }
        vinculoRepository.excluir(vinculoId);
    }

    private void validarContribuinteExiste(UUID contribuinteId) {
        if (contribuinteRepository.buscarPorId(contribuinteId).isEmpty()) {
            throw new NotFoundException("Contribuinte não encontrado.");
        }
    }
}
