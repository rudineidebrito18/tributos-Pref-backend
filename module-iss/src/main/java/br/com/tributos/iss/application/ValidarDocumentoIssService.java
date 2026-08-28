package br.com.tributos.iss.application;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.AlvaraRepository;
import br.com.tributos.iss.domain.CertidaoIssRepository;
import br.com.tributos.iss.domain.ResultadoValidacaoDocumento;
import br.com.tributos.iss.domain.TipoDocumentoIss;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class ValidarDocumentoIssService {

    private final AlvaraRepository alvaraRepository;
    private final CertidaoIssRepository certidaoIssRepository;

    public ValidarDocumentoIssService(
        AlvaraRepository alvaraRepository,
        CertidaoIssRepository certidaoIssRepository
    ) {
        this.alvaraRepository = alvaraRepository;
        this.certidaoIssRepository = certidaoIssRepository;
    }

    @Transactional(readOnly = true)
    public ResultadoValidacaoDocumento executar(String codigoVerificacao) {
        if (codigoVerificacao == null || codigoVerificacao.isBlank()) {
            throw new NotFoundException("Documento não encontrado.");
        }

        String codigo = codigoVerificacao.trim();
        LocalDate hoje = LocalDate.now();

        var alvara = alvaraRepository.buscarPorCodigoVerificacao(codigo);
        if (alvara.isPresent()) {
            var doc = alvara.get();
            return new ResultadoValidacaoDocumento(
                TipoDocumentoIss.ALVARA,
                doc.id(),
                doc.numero(),
                doc.codigoVerificacao(),
                doc.contribuinteId(),
                doc.dataEmissao(),
                doc.validade(),
                doc.vigente(hoje),
                Optional.empty()
            );
        }

        var certidao = certidaoIssRepository.buscarPorCodigoVerificacao(codigo);
        if (certidao.isPresent()) {
            var doc = certidao.get();
            return new ResultadoValidacaoDocumento(
                TipoDocumentoIss.CERTIDAO,
                doc.id(),
                doc.numero(),
                doc.codigoVerificacao(),
                doc.contribuinteId(),
                doc.dataEmissao(),
                doc.validade(),
                doc.vigente(hoje),
                Optional.of(doc.tipo())
            );
        }

        throw new NotFoundException("Documento não encontrado.");
    }
}
