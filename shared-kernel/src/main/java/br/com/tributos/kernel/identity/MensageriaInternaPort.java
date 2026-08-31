package br.com.tributos.kernel.identity;

import java.util.UUID;

public interface MensageriaInternaPort {

    void enviar(UUID destinatarioId, String assunto, String corpo);
}
