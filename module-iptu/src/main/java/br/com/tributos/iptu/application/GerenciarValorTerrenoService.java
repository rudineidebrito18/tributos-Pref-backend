package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.ValorTerrenoM2;
import br.com.tributos.iptu.domain.ValorTerrenoM2Repository;
import br.com.tributos.iptu.domain.ZonaFiscalRepository;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarValorTerrenoService {

    private final ValorTerrenoM2Repository valorTerrenoM2Repository;
    private final ZonaFiscalRepository zonaFiscalRepository;

    public GerenciarValorTerrenoService(
        ValorTerrenoM2Repository valorTerrenoM2Repository,
        ZonaFiscalRepository zonaFiscalRepository
    ) {
        this.valorTerrenoM2Repository = valorTerrenoM2Repository;
        this.zonaFiscalRepository = zonaFiscalRepository;
    }

    @Transactional(readOnly = true)
    public List<ValorTerrenoM2> listarPorExercicio(int exercicio) {
        return valorTerrenoM2Repository.listarPorExercicio(exercicio);
    }

    @Transactional
    public ValorTerrenoM2 upsert(int exercicio, UUID zonaFiscalId, BigDecimal valorM2) {
        if (!zonaFiscalRepository.existe(zonaFiscalId)) {
            throw new ValidationException("Informe uma zona fiscal válida.");
        }
        if (valorM2 == null || valorM2.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Informe o valor do terreno por m² maior que zero.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        UUID id = valorTerrenoM2Repository.buscarPorZonaEExercicio(zonaFiscalId, exercicio)
            .map(ValorTerrenoM2::id)
            .orElse(UUID.randomUUID());

        ValorTerrenoM2 valor = new ValorTerrenoM2(id, tenantId, zonaFiscalId, exercicio, valorM2);
        return valorTerrenoM2Repository.salvar(valor);
    }
}
