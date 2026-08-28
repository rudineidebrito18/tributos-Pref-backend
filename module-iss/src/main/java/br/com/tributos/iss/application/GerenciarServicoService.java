package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Servico;
import br.com.tributos.iss.domain.ServicoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarServicoService {

    private final ServicoRepository servicoRepository;

    public GerenciarServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional(readOnly = true)
    public List<Servico> listar() {
        return servicoRepository.listar();
    }

    @Transactional(readOnly = true)
    public Servico buscar(UUID id) {
        return servicoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Serviço não encontrado."));
    }

    @Transactional
    public Servico criar(
        String codigoLc116,
        String descricao,
        BigDecimal aliquotaMinima,
        BigDecimal aliquotaMaxima,
        boolean ativo
    ) {
        validarCampos(codigoLc116, descricao, aliquotaMinima, aliquotaMaxima);
        String codigoNormalizado = codigoLc116.trim();
        if (servicoRepository.existePorCodigoLc116(codigoNormalizado, null)) {
            throw new ValidationException("Já existe um serviço com este código LC 116.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        Servico servico = new Servico(
            UUID.randomUUID(), tenantId, codigoNormalizado, descricao.trim(), aliquotaMinima, aliquotaMaxima, ativo
        );
        return servicoRepository.salvar(servico);
    }

    @Transactional
    public Servico atualizar(
        UUID id,
        String codigoLc116,
        String descricao,
        BigDecimal aliquotaMinima,
        BigDecimal aliquotaMaxima,
        boolean ativo
    ) {
        validarCampos(codigoLc116, descricao, aliquotaMinima, aliquotaMaxima);
        Servico existente = buscar(id);
        String codigoNormalizado = codigoLc116.trim();
        if (servicoRepository.existePorCodigoLc116(codigoNormalizado, id)) {
            throw new ValidationException("Já existe um serviço com este código LC 116.");
        }
        Servico atualizado = new Servico(
            existente.id(),
            existente.tenantId(),
            codigoNormalizado,
            descricao.trim(),
            aliquotaMinima,
            aliquotaMaxima,
            ativo
        );
        return servicoRepository.salvar(atualizado);
    }

    @Transactional
    public void excluir(UUID id) {
        if (servicoRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Serviço não encontrado.");
        }
        servicoRepository.excluir(id);
    }

    private static void validarCampos(
        String codigoLc116,
        String descricao,
        BigDecimal aliquotaMinima,
        BigDecimal aliquotaMaxima
    ) {
        if (codigoLc116 == null || codigoLc116.isBlank()) {
            throw new ValidationException("Informe o código LC 116 do serviço.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new ValidationException("Informe a descrição do serviço.");
        }
        if (aliquotaMinima != null && aliquotaMaxima != null
            && aliquotaMinima.compareTo(aliquotaMaxima) > 0) {
            throw new ValidationException("A alíquota mínima não pode ser maior que a máxima.");
        }
    }
}
