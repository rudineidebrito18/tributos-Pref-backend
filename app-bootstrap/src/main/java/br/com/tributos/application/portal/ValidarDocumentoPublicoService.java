package br.com.tributos.application.portal;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.iss.application.ValidarDocumentoIssService;
import br.com.tributos.iss.domain.TipoCertidaoIss;
import br.com.tributos.iss.domain.TipoDocumentoIss;
import br.com.tributos.iptu.domain.HabiteseImovel;
import br.com.tributos.iptu.domain.HabiteseImovelRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class ValidarDocumentoPublicoService {

    private final ValidarDocumentoIssService validarDocumentoIssService;
    private final HabiteseImovelRepository habiteseImovelRepository;
    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;

    public ValidarDocumentoPublicoService(
        ValidarDocumentoIssService validarDocumentoIssService,
        HabiteseImovelRepository habiteseImovelRepository,
        GuiaArrecadacaoRepository guiaArrecadacaoRepository
    ) {
        this.validarDocumentoIssService = validarDocumentoIssService;
        this.habiteseImovelRepository = habiteseImovelRepository;
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
    }

    public ValidacaoDocumentoPublicoResult executar(String codigoVerificacao) {
        if (codigoVerificacao == null || codigoVerificacao.isBlank()) {
            throw new NotFoundException("Documento não encontrado.");
        }

        String codigo = codigoVerificacao.trim();
        LocalDate hoje = LocalDate.now();

        try {
            var iss = validarDocumentoIssService.executar(codigo);
            TipoDocumentoPublico tipo = iss.tipoDocumento() == TipoDocumentoIss.ALVARA
                ? TipoDocumentoPublico.ALVARA
                : TipoDocumentoPublico.CERTIDAO;
            String detalhe = iss.tipoCertidao()
                .map(TipoCertidaoIss::name)
                .orElse(null);
            return new ValidacaoDocumentoPublicoResult(
                tipo,
                iss.id(),
                iss.numero(),
                iss.codigoVerificacao(),
                iss.dataEmissao(),
                iss.validade(),
                iss.vigente(),
                detalhe
            );
        } catch (NotFoundException ignored) {
            // segue para outros tipos
        }

        var habitese = habiteseImovelRepository.buscarPorCodigoVerificacao(codigo);
        if (habitese.isPresent()) {
            return paraHabitese(habitese.get(), hoje);
        }

        var guia = guiaArrecadacaoRepository.buscarPorCodigoVerificacao(codigo);
        if (guia.isPresent()) {
            return paraDam(guia.get(), hoje);
        }

        throw new NotFoundException("Documento não encontrado.");
    }

    private static ValidacaoDocumentoPublicoResult paraHabitese(HabiteseImovel habitese, LocalDate hoje) {
        boolean vigente = habitese.validade() == null || !habitese.validade().isBefore(hoje);
        return new ValidacaoDocumentoPublicoResult(
            TipoDocumentoPublico.HABITE_SE,
            habitese.id(),
            habitese.numero(),
            habitese.codigoVerificacao(),
            habitese.dataEmissaoTs() != null ? habitese.dataEmissaoTs() : Instant.MIN,
            habitese.validade(),
            vigente,
            null
        );
    }

    private static ValidacaoDocumentoPublicoResult paraDam(GuiaArrecadacao guia, LocalDate hoje) {
        boolean vigente = guia.situacao() != SituacaoGuia.CANCELADA
            && (guia.situacao() == SituacaoGuia.PAGA || !guia.dataVencimento().isBefore(hoje));
        return new ValidacaoDocumentoPublicoResult(
            TipoDocumentoPublico.DAM,
            guia.id(),
            guia.numero(),
            guia.codigoVerificacao(),
            guia.dataEmissao(),
            guia.dataVencimento(),
            vigente,
            guia.situacao().name()
        );
    }
}
