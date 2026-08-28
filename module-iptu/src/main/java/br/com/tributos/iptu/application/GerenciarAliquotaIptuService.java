package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.adapters.out.persistence.ImovelDestinacaoJpaRepository;
import br.com.tributos.iptu.domain.AliquotaIptu;
import br.com.tributos.iptu.domain.AliquotaIptuRepository;
import br.com.tributos.iptu.domain.ZonaFiscalRepository;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarAliquotaIptuService {

    private final AliquotaIptuRepository aliquotaIptuRepository;
    private final ZonaFiscalRepository zonaFiscalRepository;
    private final ImovelDestinacaoJpaRepository imovelDestinacaoJpaRepository;

    public GerenciarAliquotaIptuService(
        AliquotaIptuRepository aliquotaIptuRepository,
        ZonaFiscalRepository zonaFiscalRepository,
        ImovelDestinacaoJpaRepository imovelDestinacaoJpaRepository
    ) {
        this.aliquotaIptuRepository = aliquotaIptuRepository;
        this.zonaFiscalRepository = zonaFiscalRepository;
        this.imovelDestinacaoJpaRepository = imovelDestinacaoJpaRepository;
    }

    @Transactional(readOnly = true)
    public List<AliquotaIptu> listarPorExercicio(int exercicio) {
        return aliquotaIptuRepository.listarPorExercicio(exercicio);
    }

    @Transactional
    public AliquotaIptu upsert(int exercicio, UUID destinacaoId, UUID zonaFiscalId, BigDecimal aliquota) {
        if (!zonaFiscalRepository.existe(zonaFiscalId)) {
            throw new ValidationException("Informe uma zona fiscal válida.");
        }
        if (!imovelDestinacaoJpaRepository.existsById(destinacaoId)) {
            throw new ValidationException("Informe uma destinação válida.");
        }
        if (aliquota == null || aliquota.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Informe uma alíquota maior que zero.");
        }

        UUID tenantId = TenantContext.getObrigatorio();
        UUID id = aliquotaIptuRepository.buscarPorChave(exercicio, destinacaoId, zonaFiscalId)
            .map(AliquotaIptu::id)
            .orElse(UUID.randomUUID());

        AliquotaIptu registro = new AliquotaIptu(id, tenantId, exercicio, destinacaoId, zonaFiscalId, aliquota);
        return aliquotaIptuRepository.salvar(registro);
    }
}
