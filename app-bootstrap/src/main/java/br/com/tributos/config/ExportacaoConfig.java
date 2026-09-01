package br.com.tributos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.tributos.shared.exportacao.ServicoExportacao;

@Configuration
public class ExportacaoConfig {

    @Bean
    ServicoExportacao servicoExportacao() {
        return new ServicoExportacao();
    }
}
