package br.com.tributos.financeiro.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.pix.conciliacao")
public record ConciliacaoPixProperties(
    boolean habilitada,
    String cron,
    int tamanhoLote,
    int diasRetroativos
) {
    public ConciliacaoPixProperties {
        if (cron == null || cron.isBlank()) {
            cron = "0 */15 * * * *";
        }
        if (tamanhoLote <= 0) {
            tamanhoLote = 100;
        }
        if (diasRetroativos <= 0) {
            diasRetroativos = 30;
        }
    }
}
