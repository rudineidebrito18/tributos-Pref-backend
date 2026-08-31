package br.com.tributos.kernel.pixbb;

import java.util.Optional;
import java.util.UUID;

/** Porta para obter a configuração PIX BB ativa do tenant — implementada em platform-identity. */
public interface ConfiguracaoPixBbPort {

    Optional<ConfiguracaoPixOperacional> buscarAtiva(UUID tenantId);
}
