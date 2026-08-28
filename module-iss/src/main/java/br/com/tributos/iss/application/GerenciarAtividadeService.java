package br.com.tributos.iss.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Atividade;
import br.com.tributos.iss.domain.AtividadeRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarAtividadeService {

    private final AtividadeRepository atividadeRepository;

    public GerenciarAtividadeService(AtividadeRepository atividadeRepository) {
        this.atividadeRepository = atividadeRepository;
    }

    @Transactional(readOnly = true)
    public List<Atividade> listar() {
        return atividadeRepository.listar();
    }

    @Transactional(readOnly = true)
    public Atividade buscar(UUID id) {
        return atividadeRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));
    }

    @Transactional
    public Atividade criar(String codigo, String descricao, boolean ativo) {
        validarCampos(codigo, descricao);
        String codigoNormalizado = codigo.trim();
        if (atividadeRepository.existePorCodigo(codigoNormalizado, null)) {
            throw new ValidationException("Já existe uma atividade com este código.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        Atividade atividade = new Atividade(UUID.randomUUID(), tenantId, codigoNormalizado, descricao.trim(), ativo);
        return atividadeRepository.salvar(atividade);
    }

    @Transactional
    public Atividade atualizar(UUID id, String codigo, String descricao, boolean ativo) {
        validarCampos(codigo, descricao);
        Atividade existente = buscar(id);
        String codigoNormalizado = codigo.trim();
        if (atividadeRepository.existePorCodigo(codigoNormalizado, id)) {
            throw new ValidationException("Já existe uma atividade com este código.");
        }
        Atividade atualizada = new Atividade(
            existente.id(), existente.tenantId(), codigoNormalizado, descricao.trim(), ativo
        );
        return atividadeRepository.salvar(atualizada);
    }

    @Transactional
    public void excluir(UUID id) {
        if (atividadeRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Atividade não encontrada.");
        }
        atividadeRepository.excluir(id);
    }

    private static void validarCampos(String codigo, String descricao) {
        if (codigo == null || codigo.isBlank()) {
            throw new ValidationException("Informe o código da atividade.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new ValidationException("Informe a descrição da atividade.");
        }
    }
}
