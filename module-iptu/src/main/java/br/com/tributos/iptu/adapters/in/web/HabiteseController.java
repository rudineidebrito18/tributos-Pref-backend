package br.com.tributos.iptu.adapters.in.web;

import java.util.List;
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

import br.com.tributos.iptu.adapters.in.web.dto.EmitirHabiteseRequest;
import br.com.tributos.iptu.adapters.in.web.dto.HabiteseImovelResponse;
import br.com.tributos.iptu.application.EmitirHabiteseService;
import br.com.tributos.iptu.application.ListarHabitesesImovelService;
import br.com.tributos.iptu.domain.EmitirHabiteseComando;

@RestController
@RequestMapping("/api/iptu/imoveis/{imovelId}/habiteses")
public class HabiteseController {

    private final ListarHabitesesImovelService listarHabitesesImovelService;
    private final EmitirHabiteseService emitirHabiteseService;

    public HabiteseController(
        ListarHabitesesImovelService listarHabitesesImovelService,
        EmitirHabiteseService emitirHabiteseService
    ) {
        this.listarHabitesesImovelService = listarHabitesesImovelService;
        this.emitirHabiteseService = emitirHabiteseService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public Page<HabiteseImovelResponse> listar(
        @PathVariable UUID imovelId,
        Pageable pageable
    ) {
        return listarHabitesesImovelService.executar(imovelId, pageable).map(HabiteseImovelResponse::de);
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<HabiteseImovelResponse> emitir(
        @PathVariable UUID imovelId,
        @Valid @RequestBody EmitirHabiteseRequest request
    ) {
        HabiteseImovelResponse resposta = HabiteseImovelResponse.de(
            emitirHabiteseService.executar(imovelId, paraComando(request))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    private static EmitirHabiteseComando paraComando(EmitirHabiteseRequest request) {
        List<EmitirHabiteseComando.ResponsavelComando> responsaveis = request.responsaveis() == null
            ? List.of()
            : request.responsaveis().stream()
                .map(item -> new EmitirHabiteseComando.ResponsavelComando(
                    item.nome(),
                    item.profissao(),
                    item.documento()
                ))
                .toList();

        return new EmitirHabiteseComando(
            request.tipoId(),
            request.dataEmissao(),
            request.ano(),
            request.validade(),
            request.contribuinteId(),
            request.areaImovel(),
            request.dataConclusao(),
            request.numeroAlvara(),
            request.dataAlvara(),
            request.validadeAlvara(),
            request.valorBaseCalculo(),
            request.desconto(),
            request.frente(),
            request.fundos(),
            request.ladoEsquerdo(),
            request.ladoDireito(),
            request.observacao(),
            responsaveis
        );
    }
}
