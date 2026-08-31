package br.com.tributos.identity.adapters.in.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import br.com.tributos.identity.adapters.in.web.dto.ConfiguracaoPixResponse;
import br.com.tributos.identity.adapters.in.web.dto.SalvarConfiguracaoPixRequest;
import br.com.tributos.identity.application.BuscarConfiguracaoPixService;
import br.com.tributos.identity.application.SalvarConfiguracaoPixComando;
import br.com.tributos.identity.application.SalvarConfiguracaoPixService;
import br.com.tributos.identity.application.TestarConexaoPixBbService;
import br.com.tributos.identity.adapters.in.web.dto.TestarConexaoPixResponse;
import br.com.tributos.identity.domain.AmbientePixBb;
import br.com.tributos.kernel.exception.ValidationException;

@RestController
@RequestMapping("/api/plataforma/configuracao-pix")
public class ConfiguracaoPixController {

    private final BuscarConfiguracaoPixService buscarConfiguracaoPixService;
    private final SalvarConfiguracaoPixService salvarConfiguracaoPixService;
    private final TestarConexaoPixBbService testarConexaoPixBbService;

    public ConfiguracaoPixController(
        BuscarConfiguracaoPixService buscarConfiguracaoPixService,
        SalvarConfiguracaoPixService salvarConfiguracaoPixService,
        TestarConexaoPixBbService testarConexaoPixBbService
    ) {
        this.buscarConfiguracaoPixService = buscarConfiguracaoPixService;
        this.salvarConfiguracaoPixService = salvarConfiguracaoPixService;
        this.testarConexaoPixBbService = testarConexaoPixBbService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN_TENANT')")
    public List<ConfiguracaoPixResponse> listar() {
        return buscarConfiguracaoPixService.listarDoTenantAtual().stream()
            .map(ConfiguracaoPixResponse::de)
            .toList();
    }

    @PutMapping("/{ambiente}")
    @PreAuthorize("hasRole('ADMIN_TENANT')")
    public ConfiguracaoPixResponse salvar(
        @PathVariable String ambiente,
        @Valid @RequestBody SalvarConfiguracaoPixRequest request
    ) {
        var salva = salvarConfiguracaoPixService.executar(new SalvarConfiguracaoPixComando(
            ambienteDe(ambiente),
            request.ativo(),
            request.clientId(),
            request.clientSecret(),
            request.developerApplicationKey(),
            request.escopos(),
            request.numeroConvenio(),
            request.chavePix(),
            request.indicadorCodigoBarras(),
            request.certificadoPath(),
            request.certificadoSenha(),
            request.webhookUrl(),
            request.webhookToken()
        ));
        return ConfiguracaoPixResponse.de(salva);
    }

    @PostMapping("/{ambiente}/testar-conexao")
    @PreAuthorize("hasRole('ADMIN_TENANT')")
    public TestarConexaoPixResponse testarConexao(@PathVariable String ambiente) {
        return TestarConexaoPixResponse.de(
            testarConexaoPixBbService.executar(ambienteDe(ambiente))
        );
    }

    private static AmbientePixBb ambienteDe(String valor) {
        try {
            return AmbientePixBb.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Ambiente inválido: \"" + valor + "\".");
        }
    }
}
