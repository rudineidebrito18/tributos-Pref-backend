package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.TipoAlvara;
import br.com.tributos.iss.domain.TipoAlvaraRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarTipoAlvaraService {

    private final TipoAlvaraRepository tipoAlvaraRepository;

    public GerenciarTipoAlvaraService(TipoAlvaraRepository tipoAlvaraRepository) {
        this.tipoAlvaraRepository = tipoAlvaraRepository;
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
    public TipoAlvara criar(String nome, BigDecimal valorBase, int diasValidade, boolean ativo) {
        validarCampos(nome, valorBase, diasValidade);
        String nomeNormalizado = nome.trim();
        if (tipoAlvaraRepository.existePorNome(nomeNormalizado, null)) {
            throw new ValidationException("Já existe um tipo de alvará com este nome.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        TipoAlvara tipo = new TipoAlvara(
            UUID.randomUUID(), tenantId, nomeNormalizado, valorBase, diasValidade, ativo
        );
        return tipoAlvaraRepository.salvar(tipo);
    }

    @Transactional
    public TipoAlvara atualizar(UUID id, String nome, BigDecimal valorBase, int diasValidade, boolean ativo) {
        validarCampos(nome, valorBase, diasValidade);
        TipoAlvara existente = buscar(id);
        String nomeNormalizado = nome.trim();
        if (tipoAlvaraRepository.existePorNome(nomeNormalizado, id)) {
            throw new ValidationException("Já existe um tipo de alvará com este nome.");
        }
        TipoAlvara atualizado = new TipoAlvara(
            existente.id(), existente.tenantId(), nomeNormalizado, valorBase, diasValidade, ativo
        );
        return tipoAlvaraRepository.salvar(atualizado);
    }

    private static void validarCampos(String nome, BigDecimal valorBase, int diasValidade) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe o nome do tipo de alvará.");
        }
        if (valorBase == null || valorBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Informe um valor base válido para o tipo de alvará.");
        }
        if (diasValidade <= 0) {
            throw new ValidationException("Os dias de validade devem ser maiores que zero.");
        }
    }
}
