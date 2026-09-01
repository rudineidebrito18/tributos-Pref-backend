package br.com.tributos.iptu.adapters.in.web;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.iptu.adapters.in.web.dto.CertidaoNegativaImovelResponse;
import br.com.tributos.iptu.adapters.in.web.dto.EmitirCertidaoNegativaRequest;
import br.com.tributos.iptu.application.EmitirCertidaoNegativaImovelService;
import br.com.tributos.iptu.application.ListarCertidoesNegativasImovelService;
import br.com.tributos.iptu.domain.EmitirCertidaoNegativaComando;

@RestController
@RequestMapping("/api/iptu/imoveis/{imovelId}/certidoes-negativas")
public class CertidaoNegativaController {

    private final ListarCertidoesNegativasImovelService listarCertidoesNegativasImovelService;
    private final EmitirCertidaoNegativaImovelService emitirCertidaoNegativaImovelService;

    public CertidaoNegativaController(
        ListarCertidoesNegativasImovelService listarCertidoesNegativasImovelService,
        EmitirCertidaoNegativaImovelService emitirCertidaoNegativaImovelService
    ) {
        this.listarCertidoesNegativasImovelService = listarCertidoesNegativasImovelService;
        this.emitirCertidaoNegativaImovelService = emitirCertidaoNegativaImovelService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<CertidaoNegativaImovelResponse> listar(
        @PathVariable UUID imovelId,
        Pageable pageable
    ) {
        return listarCertidoesNegativasImovelService.executar(imovelId, pageable)
            .map(CertidaoNegativaImovelResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<CertidaoNegativaImovelResponse> emitir(
        @PathVariable UUID imovelId,
        @Valid @RequestBody EmitirCertidaoNegativaRequest request
    ) {
        CertidaoNegativaImovelResponse resposta = CertidaoNegativaImovelResponse.de(
            emitirCertidaoNegativaImovelService.executar(
                imovelId,
                new EmitirCertidaoNegativaComando(
                    request.validade(),
                    request.situacaoCndId(),
                    request.observacao()
                )
            )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
