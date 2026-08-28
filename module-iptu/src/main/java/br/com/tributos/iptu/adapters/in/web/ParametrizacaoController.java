package br.com.tributos.iptu.adapters.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.iptu.adapters.in.web.dto.ParametrizacaoExercicioStatusResponse;
import br.com.tributos.iptu.application.VerificarParametrizacaoExercicioService;

@RestController
@RequestMapping("/api/iptu/exercicios/{exercicio}/parametrizacao")
public class ParametrizacaoController {

    private final VerificarParametrizacaoExercicioService verificarParametrizacaoExercicioService;

    public ParametrizacaoController(VerificarParametrizacaoExercicioService verificarParametrizacaoExercicioService) {
        this.verificarParametrizacaoExercicioService = verificarParametrizacaoExercicioService;
    }

    @PreAuthorize("hasAnyRole('ADMIN_TENANT', 'FISCAL', 'ATENDENTE')")
    @GetMapping("/status")
    public ParametrizacaoExercicioStatusResponse status(@PathVariable int exercicio) {
        return ParametrizacaoExercicioStatusResponse.de(verificarParametrizacaoExercicioService.executar(exercicio));
    }
}
