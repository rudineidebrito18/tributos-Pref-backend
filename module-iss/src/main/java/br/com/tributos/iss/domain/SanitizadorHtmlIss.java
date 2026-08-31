package br.com.tributos.iss.domain;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public final class SanitizadorHtmlIss {

    private static final PolicyFactory POLITICA = Sanitizers.FORMATTING
        .and(Sanitizers.BLOCKS)
        .and(Sanitizers.LINKS)
        .and(Sanitizers.TABLES);

    private SanitizadorHtmlIss() {
    }

    public static String sanitizar(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        return POLITICA.sanitize(html);
    }
}
