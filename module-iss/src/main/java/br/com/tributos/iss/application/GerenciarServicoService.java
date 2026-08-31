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
    public Servico criar(SalvarServicoComando comando) {
        validarCampos(comando);
        String codigoNormalizado = comando.codigoLc116().trim();
        if (servicoRepository.existePorCodigoLc116(codigoNormalizado, null)) {
            throw new ValidationException("Já existe um serviço com este código LC 116.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        Servico servico = new Servico(
            UUID.randomUUID(),
            tenantId,
            codigoNormalizado,
            comando.descricao().trim(),
            comando.aliquotaMinima(),
            comando.aliquotaMaxima(),
            comando.ativo(),
            comando.grupoServicoId(),
            normalizarOpcional(comando.codigoNbs()),
            normalizarOpcional(comando.codigoTributacaoNacional()),
            normalizarOpcional(comando.indop()),
            normalizarOpcional(comando.cClassTrib())
        );
        return servicoRepository.salvar(servico);
    }

    @Transactional
    public Servico atualizar(UUID id, SalvarServicoComando comando) {
        validarCampos(comando);
        Servico existente = buscar(id);
        String codigoNormalizado = comando.codigoLc116().trim();
        if (servicoRepository.existePorCodigoLc116(codigoNormalizado, id)) {
            throw new ValidationException("Já existe um serviço com este código LC 116.");
        }
        Servico atualizado = new Servico(
            existente.id(),
            existente.tenantId(),
            codigoNormalizado,
            comando.descricao().trim(),
            comando.aliquotaMinima(),
            comando.aliquotaMaxima(),
            comando.ativo(),
            comando.grupoServicoId(),
            normalizarOpcional(comando.codigoNbs()),
            normalizarOpcional(comando.codigoTributacaoNacional()),
            normalizarOpcional(comando.indop()),
            normalizarOpcional(comando.cClassTrib())
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

    private static void validarCampos(SalvarServicoComando comando) {
        if (comando.codigoLc116() == null || comando.codigoLc116().isBlank()) {
            throw new ValidationException("Informe o código LC 116 do serviço.");
        }
        if (comando.descricao() == null || comando.descricao().isBlank()) {
            throw new ValidationException("Informe a descrição do serviço.");
        }
        if (comando.aliquotaMinima() != null && comando.aliquotaMaxima() != null
            && comando.aliquotaMinima().compareTo(comando.aliquotaMaxima()) > 0) {
            throw new ValidationException("A alíquota mínima não pode ser maior que a máxima.");
        }
    }

    private static String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    public record SalvarServicoComando(
        String codigoLc116,
        String descricao,
        BigDecimal aliquotaMinima,
        BigDecimal aliquotaMaxima,
        boolean ativo,
        UUID grupoServicoId,
        String codigoNbs,
        String codigoTributacaoNacional,
        String indop,
        String cClassTrib
    ) {
    }
}
