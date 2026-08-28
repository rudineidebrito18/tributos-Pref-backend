package br.com.tributos.cadastro.application.ports;

import java.io.InputStream;
import java.util.UUID;

/**
 * Porta de saída para persistência de bytes de documentos — implementação local em dev,
 * substituível por S3/R2 em produção sem alterar casos de uso.
 */
public interface ArmazenamentoArquivo {

    String salvar(UUID tenantId, UUID pessoaId, UUID documentoId, String nomeArquivo, InputStream conteudo);

    InputStream ler(String storageChave);

    void excluir(String storageChave);
}
