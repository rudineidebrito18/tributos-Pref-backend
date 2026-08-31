package br.com.tributos.cadastro.application;

import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.application.ports.UsuarioAutenticadoPort;
import br.com.tributos.cadastro.application.ports.ArmazenamentoArquivo;
import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class BaixarDocumentoSistemaService {

    private final DocumentoRepository documentoRepository;
    private final ArmazenamentoArquivo armazenamentoArquivo;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;

    public BaixarDocumentoSistemaService(
        DocumentoRepository documentoRepository,
        ArmazenamentoArquivo armazenamentoArquivo,
        UsuarioAutenticadoPort usuarioAutenticadoPort
    ) {
        this.documentoRepository = documentoRepository;
        this.armazenamentoArquivo = armazenamentoArquivo;
        this.usuarioAutenticadoPort = usuarioAutenticadoPort;
    }

    @Transactional(readOnly = true)
    public ArquivoParaDownload executar(UUID documentoId) {
        UUID tenantId = TenantContext.getObrigatorio();
        UUID usuarioId = usuarioAutenticadoPort.usuarioIdAtualObrigatorio();

        Documento documento = documentoRepository.buscarPorId(documentoId)
            .filter(d -> d.tenantId().equals(tenantId) && d.pessoaId() == null)
            .orElseThrow(() -> new NotFoundException("Documento não encontrado."));

        if (!documentoRepository.possuiCompartilhamento(documento.id(), usuarioId)) {
            throw new NotFoundException("Documento não encontrado.");
        }

        return new ArquivoParaDownload(
            documento.nomeArquivo(),
            MediaType.parseMediaType(documento.conteudoTipo()),
            new InputStreamResource(armazenamentoArquivo.ler(documento.storageChave()))
        );
    }

    public record ArquivoParaDownload(String nomeArquivo, MediaType conteudoTipo, InputStreamResource recurso) {
    }
}
