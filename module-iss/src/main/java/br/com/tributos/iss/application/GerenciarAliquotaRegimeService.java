package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.adapters.out.persistence.RegimeTributarioJpaRepository;
import br.com.tributos.iss.domain.AliquotaRegime;
import br.com.tributos.iss.domain.AliquotaRegimeRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarAliquotaRegimeService {

    private final AliquotaRegimeRepository aliquotaRegimeRepository;
    private final RegimeTributarioJpaRepository regimeTributarioJpaRepository;

    public GerenciarAliquotaRegimeService(
        AliquotaRegimeRepository aliquotaRegimeRepository,
        RegimeTributarioJpaRepository regimeTributarioJpaRepository
    ) {
        this.aliquotaRegimeRepository = aliquotaRegimeRepository;
        this.regimeTributarioJpaRepository = regimeTributarioJpaRepository;
    }

    @Transactional(readOnly = true)
    public List<AliquotaRegime> listarPorRegime(UUID regimeId) {
        validarRegimeExiste(regimeId);
        return aliquotaRegimeRepository.listarPorRegime(regimeId);
    }

    @Transactional(readOnly = true)
    public AliquotaRegime buscar(UUID regimeId, UUID id) {
        validarRegimeExiste(regimeId);
        AliquotaRegime faixa = aliquotaRegimeRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Faixa de alíquota não encontrada."));
        if (!faixa.regimeId().equals(regimeId)) {
            throw new NotFoundException("Faixa de alíquota não encontrada para este regime.");
        }
        return faixa;
    }

    @Transactional
    public AliquotaRegime criar(
        UUID regimeId,
        BigDecimal faixaReceitaMin,
        BigDecimal faixaReceitaMax,
        BigDecimal aliquotaNominal,
        BigDecimal parcelaDeduzir,
        BigDecimal percentualIss,
        LocalDate competenciaVigencia,
        String anexoSimples
    ) {
        validarRegimeExiste(regimeId);
        validarCampos(faixaReceitaMin, aliquotaNominal, parcelaDeduzir, percentualIss, competenciaVigencia);

        UUID tenantId = TenantContext.getObrigatorio();
        AliquotaRegime faixa = new AliquotaRegime(
            UUID.randomUUID(),
            tenantId,
            regimeId,
            faixaReceitaMin,
            faixaReceitaMax,
            aliquotaNominal,
            parcelaDeduzir != null ? parcelaDeduzir : BigDecimal.ZERO,
            percentualIss != null ? percentualIss : new BigDecimal("33.5"),
            competenciaVigencia,
            anexoSimples
        );
        return aliquotaRegimeRepository.salvar(faixa);
    }

    @Transactional
    public AliquotaRegime atualizar(
        UUID regimeId,
        UUID id,
        BigDecimal faixaReceitaMin,
        BigDecimal faixaReceitaMax,
        BigDecimal aliquotaNominal,
        BigDecimal parcelaDeduzir,
        BigDecimal percentualIss,
        LocalDate competenciaVigencia,
        String anexoSimples
    ) {
        AliquotaRegime existente = buscar(regimeId, id);
        validarCampos(faixaReceitaMin, aliquotaNominal, parcelaDeduzir, percentualIss, competenciaVigencia);

        AliquotaRegime atualizada = new AliquotaRegime(
            existente.id(),
            existente.tenantId(),
            regimeId,
            faixaReceitaMin,
            faixaReceitaMax,
            aliquotaNominal,
            parcelaDeduzir != null ? parcelaDeduzir : BigDecimal.ZERO,
            percentualIss != null ? percentualIss : new BigDecimal("33.5"),
            competenciaVigencia,
            anexoSimples
        );
        return aliquotaRegimeRepository.salvar(atualizada);
    }

    @Transactional
    public void excluir(UUID regimeId, UUID id) {
        buscar(regimeId, id);
        aliquotaRegimeRepository.excluir(id);
    }

    private void validarRegimeExiste(UUID regimeId) {
        if (!regimeTributarioJpaRepository.existsById(regimeId)) {
            throw new NotFoundException("Regime tributário não encontrado.");
        }
    }

    private static void validarCampos(
        BigDecimal faixaReceitaMin,
        BigDecimal aliquotaNominal,
        BigDecimal parcelaDeduzir,
        BigDecimal percentualIss,
        LocalDate competenciaVigencia
    ) {
        if (faixaReceitaMin == null) {
            throw new ValidationException("Informe a receita mínima da faixa.");
        }
        if (aliquotaNominal == null) {
            throw new ValidationException("Informe a alíquota nominal da faixa.");
        }
        if (competenciaVigencia == null) {
            throw new ValidationException("Informe a competência de vigência da faixa.");
        }
        if (parcelaDeduzir != null && parcelaDeduzir.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("A parcela a deduzir não pode ser negativa.");
        }
        if (percentualIss != null && percentualIss.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("O percentual de ISS deve ser maior que zero.");
        }
    }
}
