package br.com.tributos.iptu.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.adapters.out.persistence.ImovelDestinacaoJpaEntity;
import br.com.tributos.iptu.adapters.out.persistence.ImovelDestinacaoJpaRepository;
import br.com.tributos.iptu.domain.AliquotaIptu;
import br.com.tributos.iptu.domain.AliquotaIptuRepository;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.ParametrizacaoExercicioStatus;
import br.com.tributos.iptu.domain.ParametrizacaoExercicioStatus.CombinacaoAliquotaFaltante;
import br.com.tributos.iptu.domain.ValorTerrenoM2;
import br.com.tributos.iptu.domain.ValorTerrenoM2Repository;
import br.com.tributos.iptu.domain.ZonaFiscal;
import br.com.tributos.iptu.domain.ZonaFiscalRepository;

@Service
public class VerificarParametrizacaoExercicioService {

    private final ZonaFiscalRepository zonaFiscalRepository;
    private final ValorTerrenoM2Repository valorTerrenoM2Repository;
    private final AliquotaIptuRepository aliquotaIptuRepository;
    private final ImovelDestinacaoJpaRepository imovelDestinacaoJpaRepository;
    private final ImovelRepository imovelRepository;

    public VerificarParametrizacaoExercicioService(
        ZonaFiscalRepository zonaFiscalRepository,
        ValorTerrenoM2Repository valorTerrenoM2Repository,
        AliquotaIptuRepository aliquotaIptuRepository,
        ImovelDestinacaoJpaRepository imovelDestinacaoJpaRepository,
        ImovelRepository imovelRepository
    ) {
        this.zonaFiscalRepository = zonaFiscalRepository;
        this.valorTerrenoM2Repository = valorTerrenoM2Repository;
        this.aliquotaIptuRepository = aliquotaIptuRepository;
        this.imovelDestinacaoJpaRepository = imovelDestinacaoJpaRepository;
        this.imovelRepository = imovelRepository;
    }

    @Transactional(readOnly = true)
    public ParametrizacaoExercicioStatus executar(int exercicio) {
        List<ZonaFiscal> zonasAtivas = zonaFiscalRepository.listarAtivas();
        boolean zonasOk = !zonasAtivas.isEmpty();

        List<ValorTerrenoM2> valoresTerreno = valorTerrenoM2Repository.listarPorExercicio(exercicio);
        Set<UUID> zonasComValor = valoresTerreno.stream().map(ValorTerrenoM2::zonaFiscalId).collect(Collectors.toSet());
        boolean valoresTerrenoOk = zonasOk && zonasAtivas.stream().allMatch(z -> zonasComValor.contains(z.id()));

        List<ImovelDestinacaoJpaEntity> destinacoesAtivas = imovelDestinacaoJpaRepository.findByAtivoTrueOrderByNome();
        List<AliquotaIptu> aliquotas = aliquotaIptuRepository.listarPorExercicio(exercicio);
        Set<String> chavesAliquota = aliquotas.stream()
            .map(a -> a.destinacaoId() + ":" + a.zonaFiscalId())
            .collect(Collectors.toSet());

        List<CombinacaoAliquotaFaltante> combinacoesFaltantes = new ArrayList<>();
        for (ImovelDestinacaoJpaEntity destinacao : destinacoesAtivas) {
            for (ZonaFiscal zona : zonasAtivas) {
                String chave = destinacao.getId() + ":" + zona.id();
                if (!chavesAliquota.contains(chave)) {
                    combinacoesFaltantes.add(new CombinacaoAliquotaFaltante(destinacao.getId(), zona.id()));
                }
            }
        }

        boolean aliquotasOk = combinacoesFaltantes.isEmpty();
        long imoveisSemZona = imovelRepository.contarAtivosSemZona();
        boolean completo = zonasOk && valoresTerrenoOk && aliquotasOk && imoveisSemZona == 0;

        return new ParametrizacaoExercicioStatus(
            exercicio,
            zonasOk,
            valoresTerrenoOk,
            aliquotasOk,
            imoveisSemZona,
            combinacoesFaltantes,
            completo
        );
    }
}
