package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.adapters.out.persistence.RegimeTributarioJpaRepository;
import br.com.tributos.iss.domain.AliquotaRegimeRepository;
import br.com.tributos.iss.domain.CalculadorAliquotaSimplesNacional;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class CalcularAliquotaEfetivaService {

    private final AliquotaRegimeRepository aliquotaRegimeRepository;
    private final RegimeTributarioJpaRepository regimeTributarioJpaRepository;

    public CalcularAliquotaEfetivaService(
        AliquotaRegimeRepository aliquotaRegimeRepository,
        RegimeTributarioJpaRepository regimeTributarioJpaRepository
    ) {
        this.aliquotaRegimeRepository = aliquotaRegimeRepository;
        this.regimeTributarioJpaRepository = regimeTributarioJpaRepository;
    }

    @Transactional(readOnly = true)
    public CalculadorAliquotaSimplesNacional.Resultado calcular(
        UUID regimeId,
        BigDecimal receitaBrutaAcumulada12Meses,
        LocalDate competencia
    ) {
        if (!regimeTributarioJpaRepository.existsById(regimeId)) {
            throw new NotFoundException("Regime tributário não encontrado.");
        }
        if (receitaBrutaAcumulada12Meses == null) {
            throw new ValidationException("Informe a receita bruta acumulada dos últimos 12 meses.");
        }

        LocalDate competenciaCalculo = competencia != null ? competencia : LocalDate.now();
        var faixas = aliquotaRegimeRepository.listarVigentesPorRegime(regimeId, competenciaCalculo).stream()
            .map(faixa -> faixa.paraFaixa())
            .toList();

        if (faixas.isEmpty()) {
            throw new ValidationException("Nenhuma faixa de alíquota vigente configurada para o regime.");
        }

        try {
            return CalculadorAliquotaSimplesNacional.calcular(receitaBrutaAcumulada12Meses, faixas);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }
}
