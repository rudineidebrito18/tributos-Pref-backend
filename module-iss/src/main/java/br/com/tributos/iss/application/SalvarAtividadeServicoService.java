package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.AtividadeRepository;
import br.com.tributos.iss.domain.AtividadeServico;
import br.com.tributos.iss.domain.AtividadeServicoRepository;
import br.com.tributos.iss.domain.LocalIncidenciaRepository;
import br.com.tributos.iss.domain.ServicoRepository;
import br.com.tributos.kernel.audit.AuditoriaPort;
import br.com.tributos.kernel.audit.RegistroAuditoria;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class SalvarAtividadeServicoService {

    private final AtividadeServicoRepository atividadeServicoRepository;
    private final AtividadeRepository atividadeRepository;
    private final ServicoRepository servicoRepository;
    private final LocalIncidenciaRepository localIncidenciaRepository;
    private final AuditoriaPort auditoriaPort;

    public SalvarAtividadeServicoService(
        AtividadeServicoRepository atividadeServicoRepository,
        AtividadeRepository atividadeRepository,
        ServicoRepository servicoRepository,
        LocalIncidenciaRepository localIncidenciaRepository,
        AuditoriaPort auditoriaPort
    ) {
        this.atividadeServicoRepository = atividadeServicoRepository;
        this.atividadeRepository = atividadeRepository;
        this.servicoRepository = servicoRepository;
        this.localIncidenciaRepository = localIncidenciaRepository;
        this.auditoriaPort = auditoriaPort;
    }

    @Transactional
    public AtividadeServico criar(SalvarAtividadeServicoComando comando) {
        validarReferencias(comando);
        if (atividadeServicoRepository.existePorAtividadeEServico(
            comando.atividadeId(), comando.servicoId(), null
        )) {
            throw new ValidationException("Já existe um vínculo cadastrado para esta atividade e serviço.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        AtividadeServico novo = new AtividadeServico(
            UUID.randomUUID(),
            tenantId,
            comando.atividadeId(),
            comando.servicoId(),
            comando.localIncidenciaId(),
            comando.aliquota(),
            comando.tributavel(),
            comando.imune(),
            comando.deducao(),
            comando.substitutoTributario(),
            comando.retencaoFonte(),
            comando.regimeEspecial(),
            comando.observacao()
        );
        return atividadeServicoRepository.salvar(novo);
    }

    @Transactional
    public AtividadeServico atualizar(UUID id, SalvarAtividadeServicoComando comando) {
        validarReferencias(comando);
        AtividadeServico existente = atividadeServicoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Vínculo atividade/serviço não encontrado."));

        if (atividadeServicoRepository.existePorAtividadeEServico(
            comando.atividadeId(), comando.servicoId(), id
        )) {
            throw new ValidationException("Já existe um vínculo cadastrado para esta atividade e serviço.");
        }

        BigDecimal aliquotaAnterior = existente.aliquota();
        AtividadeServico atualizado = new AtividadeServico(
            existente.id(),
            existente.tenantId(),
            comando.atividadeId(),
            comando.servicoId(),
            comando.localIncidenciaId(),
            comando.aliquota(),
            comando.tributavel(),
            comando.imune(),
            comando.deducao(),
            comando.substitutoTributario(),
            comando.retencaoFonte(),
            comando.regimeEspecial(),
            comando.observacao()
        );
        AtividadeServico salvo = atividadeServicoRepository.salvar(atualizado);

        if (aliquotaAnterior.compareTo(comando.aliquota()) != 0) {
            auditoriaPort.registrar(new RegistroAuditoria(
                "iss_atividade_servico",
                id.toString(),
                "ALTERAR_ALIQUOTA",
                Map.of("aliquota", aliquotaAnterior),
                Map.of("aliquota", comando.aliquota())
            ));
        }

        return salvo;
    }

    private void validarReferencias(SalvarAtividadeServicoComando comando) {
        if (comando.aliquota() == null) {
            throw new ValidationException("Informe a alíquota.");
        }
        if (atividadeRepository.buscarPorId(comando.atividadeId()).isEmpty()) {
            throw new NotFoundException("Atividade não encontrada.");
        }
        if (comando.servicoId() != null && servicoRepository.buscarPorId(comando.servicoId()).isEmpty()) {
            throw new NotFoundException("Serviço não encontrado.");
        }
        if (localIncidenciaRepository.buscarPorId(comando.localIncidenciaId()).isEmpty()) {
            throw new NotFoundException("Local de incidência não encontrado.");
        }
    }
}
