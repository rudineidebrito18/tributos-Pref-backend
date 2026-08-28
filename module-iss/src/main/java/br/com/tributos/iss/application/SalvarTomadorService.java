package br.com.tributos.iss.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.PessoaReferenciaRepository;
import br.com.tributos.iss.domain.Tomador;
import br.com.tributos.iss.domain.TomadorRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class SalvarTomadorService {

    private final TomadorRepository tomadorRepository;
    private final PessoaReferenciaRepository pessoaReferenciaRepository;

    public SalvarTomadorService(
        TomadorRepository tomadorRepository,
        PessoaReferenciaRepository pessoaReferenciaRepository
    ) {
        this.tomadorRepository = tomadorRepository;
        this.pessoaReferenciaRepository = pessoaReferenciaRepository;
    }

    @Transactional
    public Tomador executar(UUID pessoaId) {
        if (!pessoaReferenciaRepository.existe(pessoaId)) {
            throw new ValidationException("Pessoa não encontrada para vincular ao tomador.");
        }
        if (tomadorRepository.existePorPessoaId(pessoaId, null)) {
            throw new ValidationException("Esta pessoa já possui cadastro de tomador.");
        }

        Tomador tomador = new Tomador(UUID.randomUUID(), TenantContext.getObrigatorio(), pessoaId);
        return tomadorRepository.salvar(tomador);
    }
}
