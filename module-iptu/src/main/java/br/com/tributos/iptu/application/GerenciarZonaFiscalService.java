package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.ZonaFiscal;
import br.com.tributos.iptu.domain.ZonaFiscalRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarZonaFiscalService {

    private final ZonaFiscalRepository zonaFiscalRepository;

    public GerenciarZonaFiscalService(ZonaFiscalRepository zonaFiscalRepository) {
        this.zonaFiscalRepository = zonaFiscalRepository;
    }

    @Transactional(readOnly = true)
    public List<ZonaFiscal> listar() {
        return zonaFiscalRepository.listar();
    }

    @Transactional(readOnly = true)
    public ZonaFiscal buscar(UUID id) {
        return zonaFiscalRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Zona fiscal não encontrada."));
    }

    @Transactional
    public ZonaFiscal criar(String nome, BigDecimal fatorValorizacao, boolean ativo) {
        validarNome(nome);
        validarFator(fatorValorizacao);
        if (zonaFiscalRepository.existePorNome(nome.trim(), null)) {
            throw new ValidationException("Já existe uma zona fiscal com este nome.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        BigDecimal fator = fatorValorizacao != null ? fatorValorizacao : BigDecimal.ONE;
        ZonaFiscal zona = new ZonaFiscal(UUID.randomUUID(), tenantId, nome.trim(), fator, ativo);
        return zonaFiscalRepository.salvar(zona);
    }

    @Transactional
    public ZonaFiscal atualizar(UUID id, String nome, BigDecimal fatorValorizacao, boolean ativo) {
        ZonaFiscal existente = buscar(id);
        validarNome(nome);
        validarFator(fatorValorizacao);
        if (zonaFiscalRepository.existePorNome(nome.trim(), id)) {
            throw new ValidationException("Já existe uma zona fiscal com este nome.");
        }

        BigDecimal fator = fatorValorizacao != null ? fatorValorizacao : BigDecimal.ONE;
        ZonaFiscal atualizada = new ZonaFiscal(existente.id(), existente.tenantId(), nome.trim(), fator, ativo);
        return zonaFiscalRepository.salvar(atualizada);
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe o nome da zona fiscal.");
        }
    }

    private static void validarFator(BigDecimal fatorValorizacao) {
        if (fatorValorizacao != null && fatorValorizacao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("O fator de valorização deve ser maior que zero.");
        }
    }
}
