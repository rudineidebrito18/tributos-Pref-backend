package br.com.tributos.kernel.cadastro;

import java.util.Optional;
import java.util.UUID;

/** Porta para dados do devedor — implementada em module-cadastro. */
public interface DevedorPixPort {

    Optional<DadosDevedorPix> buscarPorPessoaId(UUID pessoaId);
}
