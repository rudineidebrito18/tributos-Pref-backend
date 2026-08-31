package br.com.tributos.cadastro.application;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.tributos.cadastro.application.ports.ArmazenamentoArquivo;
import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoRepository;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class AnexarDocumentoInstitucionalService {

    private static final Set<String> TIPOS_MIME_PERMITIDOS = Set.of(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "image/webp"
    );

    private final DocumentoRepository documentoRepository;
    private final ArmazenamentoArquivo armazenamentoArquivo;
    private final long tamanhoMaximoBytes;

    public AnexarDocumentoInstitucionalService(
        DocumentoRepository documentoRepository,
        ArmazenamentoArquivo armazenamentoArquivo,
        @Value("${app.cadastro.documentos.tamanho-maximo-mb:10}") int tamanhoMaximoMb
    ) {
        this.documentoRepository = documentoRepository;
        this.armazenamentoArquivo = armazenamentoArquivo;
        this.tamanhoMaximoBytes = (long) tamanhoMaximoMb * 1024 * 1024;
    }

    @Transactional
    public Documento executar(String tipo, MultipartFile arquivo) {
        validarArquivo(arquivo);

        UUID tenantId = TenantContext.getObrigatorio();
        UUID documentoId = UUID.randomUUID();
        String nomeArquivo = arquivo.getOriginalFilename() != null ? arquivo.getOriginalFilename() : "documento";
        String conteudoTipo = arquivo.getContentType() != null ? arquivo.getContentType() : "application/octet-stream";

        String storageChave;
        try (InputStream stream = arquivo.getInputStream()) {
            storageChave = armazenamentoArquivo.salvar(tenantId, null, documentoId, nomeArquivo, stream);
        } catch (IOException ex) {
            throw new ValidationException("Não foi possível ler o arquivo enviado.");
        }

        return documentoRepository.salvar(new Documento(
            documentoId, tenantId, null, tipo.trim(), null, null, nomeArquivo, conteudoTipo,
            arquivo.getSize(), storageChave, false, Instant.now()
        ));
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidationException("Selecione um arquivo para anexar.");
        }
        if (arquivo.getSize() > tamanhoMaximoBytes) {
            throw new ValidationException("Arquivo excede o tamanho máximo permitido.");
        }
        String mime = arquivo.getContentType();
        if (mime == null || !TIPOS_MIME_PERMITIDOS.contains(mime)) {
            throw new ValidationException("Tipo de arquivo não permitido. Use PDF ou imagem (JPEG, PNG, WebP).");
        }
    }
}
