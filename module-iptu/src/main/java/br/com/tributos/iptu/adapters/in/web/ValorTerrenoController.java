package br.com.tributos.iptu.adapters.in.web;

import java.util.List;

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

import br.com.tributos.iptu.adapters.in.web.dto.UpsertValorTerrenoRequest;
import br.com.tributos.iptu.adapters.in.web.dto.ValorTerrenoM2Response;
import br.com.tributos.iptu.application.GerenciarValorTerrenoService;

@RestController
@RequestMapping("/api/iptu/exercicios/{exercicio}/valores-terreno")
public class ValorTerrenoController {

    private final GerenciarValorTerrenoService gerenciarValorTerrenoService;

    public ValorTerrenoController(GerenciarValorTerrenoService gerenciarValorTerrenoService) {
        this.gerenciarValorTerrenoService = gerenciarValorTerrenoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping
    public List<ValorTerrenoM2Response> listar(@PathVariable int exercicio) {
        return gerenciarValorTerrenoService.listarPorExercicio(exercicio).stream()
            .map(ValorTerrenoM2Response::de)
            .toList();
    }

    @PreAuthorize("hasRole('ADMIN_TENANT')")
    @PostMapping
    public ResponseEntity<ValorTerrenoM2Response> upsert(
        @PathVariable int exercicio,
        @Valid @RequestBody UpsertValorTerrenoRequest request
    ) {
        ValorTerrenoM2Response resposta = ValorTerrenoM2Response.de(
            gerenciarValorTerrenoService.upsert(exercicio, request.zonaFiscalId(), request.valorM2())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
