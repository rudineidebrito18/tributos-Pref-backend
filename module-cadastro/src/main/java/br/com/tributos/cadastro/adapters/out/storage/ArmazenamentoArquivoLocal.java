package br.com.tributos.cadastro.adapters.out.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.application.ports.ArmazenamentoArquivo;
import br.com.tributos.kernel.exception.ValidationException;

/**
 * Armazena arquivos no disco local — adequado para desenvolvimento. Em produção, trocar por
 * adapter S3/R2 implementando a mesma porta {@link ArmazenamentoArquivo}.
 */
@Component
public class ArmazenamentoArquivoLocal implements ArmazenamentoArquivo {

    private final Path diretorioBase;

    public ArmazenamentoArquivoLocal(
        @Value("${app.cadastro.documentos.diretorio-base:./data/documentos}") String diretorioBase
    ) {
        this.diretorioBase = Path.of(diretorioBase).toAbsolutePath().normalize();
    }

    @Override
    public String salvar(UUID tenantId, UUID pessoaId, UUID documentoId, String nomeArquivo, InputStream conteudo) {
        String segmentoPessoa = pessoaId != null ? pessoaId.toString() : "institucional";
        String chave = tenantId + "/" + segmentoPessoa + "/" + documentoId + "/" + sanitizarNome(nomeArquivo);
        Path destino = diretorioBase.resolve(chave).normalize();
        if (!destino.startsWith(diretorioBase)) {
            throw new ValidationException("Nome de arquivo inválido.");
        }
        try {
            Files.createDirectories(destino.getParent());
            Files.copy(conteudo, destino, StandardCopyOption.REPLACE_EXISTING);
            return chave;
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível salvar o documento.", ex);
        }
    }

    @Override
    public InputStream ler(String storageChave) {
        Path arquivo = diretorioBase.resolve(storageChave).normalize();
        if (!arquivo.startsWith(diretorioBase)) {
            throw new ValidationException("Chave de armazenamento inválida.");
        }
        try {
            return Files.newInputStream(arquivo);
        } catch (IOException ex) {
            throw new IllegalStateException("Arquivo não encontrado no armazenamento.", ex);
        }
    }

    @Override
    public void excluir(String storageChave) {
        Path arquivo = diretorioBase.resolve(storageChave).normalize();
        if (!arquivo.startsWith(diretorioBase)) {
            return;
        }
        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível excluir o arquivo.", ex);
        }
    }

    private static String sanitizarNome(String nomeArquivo) {
        String base = nomeArquivo == null ? "arquivo" : nomeArquivo;
        return base.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
