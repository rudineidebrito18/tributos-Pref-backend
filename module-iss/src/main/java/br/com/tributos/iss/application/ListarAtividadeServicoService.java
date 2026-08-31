package br.com.tributos.iss.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.AtividadeServico;
import br.com.tributos.iss.domain.AtividadeServicoRepository;
import br.com.tributos.iss.domain.AtividadeServicoRepository.AtividadeServicoView;

@Service
public class ListarAtividadeServicoService {

    private final AtividadeServicoRepository atividadeServicoRepository;

    public ListarAtividadeServicoService(AtividadeServicoRepository atividadeServicoRepository) {
        this.atividadeServicoRepository = atividadeServicoRepository;
    }

    @Transactional(readOnly = true)
    public Page<AtividadeServico> executar(String codigoCnae, String codigoServico, Pageable pageable) {
        return atividadeServicoRepository.listar(normalizar(codigoCnae), normalizar(codigoServico), pageable);
    }

    @Transactional(readOnly = true)
    public Page<AtividadeServicoView> executarView(String codigoCnae, String codigoServico, Pageable pageable) {
        return atividadeServicoRepository.listarView(normalizar(codigoCnae), normalizar(codigoServico), pageable);
    }

    private static String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return "%" + valor.trim() + "%";
    }
}
