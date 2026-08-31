package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.Alvara;
import br.com.tributos.iss.domain.AlvaraRepository;
import br.com.tributos.iss.domain.SituacaoFiscalAlvara;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class IsentarAlvaraService {

    private final AlvaraRepository alvaraRepository;

    public IsentarAlvaraService(AlvaraRepository alvaraRepository) {
        this.alvaraRepository = alvaraRepository;
    }

    @Transactional
    public Alvara executar(UUID alvaraId) {
        Alvara alvara = alvaraRepository.buscarPorId(alvaraId)
            .orElseThrow(() -> new NotFoundException("Alvará não encontrado."));

        if (alvara.situacaoFiscal() == SituacaoFiscalAlvara.CANCELADA) {
            throw new ValidationException("Não é possível isentar um alvará cancelado.");
        }

        return alvaraRepository.salvar(CancelarAlvaraService.copiar(alvara, SituacaoFiscalAlvara.ISENTA, alvara.motivoCancelamento()));
    }
}
