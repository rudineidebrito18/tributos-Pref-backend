package br.com.tributos.itbi.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.itbi.domain.ContribuinteReferenciaRepository;
import br.com.tributos.itbi.domain.GuiaItbiRepository;
import br.com.tributos.itbi.domain.PapelParteTransmissao;
import br.com.tributos.itbi.domain.ParteTransmissao;
import br.com.tributos.itbi.domain.ParteTransmissaoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarParteTransmissaoService {

    private final GuiaItbiRepository guiaItbiRepository;
    private final ParteTransmissaoRepository parteTransmissaoRepository;
    private final ContribuinteReferenciaRepository contribuinteReferenciaRepository;

    public GerenciarParteTransmissaoService(
        GuiaItbiRepository guiaItbiRepository,
        ParteTransmissaoRepository parteTransmissaoRepository,
        ContribuinteReferenciaRepository contribuinteReferenciaRepository
    ) {
        this.guiaItbiRepository = guiaItbiRepository;
        this.parteTransmissaoRepository = parteTransmissaoRepository;
        this.contribuinteReferenciaRepository = contribuinteReferenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<ParteTransmissao> listar(UUID guiaId, PapelParteTransmissao papel) {
        validarGuiaExiste(guiaId);
        return parteTransmissaoRepository.listarPorGuiaEPapel(guiaId, papel);
    }

    @Transactional
    public ParteTransmissao adicionar(UUID guiaId, AdicionarParteTransmissaoComando comando) {
        validarGuiaExiste(guiaId);

        if (comando.porcentagem() == null) {
            throw new ValidationException("Informe a porcentagem da parte.");
        }

        if (!contribuinteReferenciaRepository.existe(comando.contribuinteId())) {
            throw new ValidationException("Contribuinte não encontrado.");
        }

        if (parteTransmissaoRepository.existePorGuiaContribuinteEPapel(
            guiaId, comando.contribuinteId(), comando.papel(), null
        )) {
            throw new ValidationException("Este contribuinte já está vinculado a esta guia com o mesmo papel.");
        }

        List<ParteTransmissao> atuais = new ArrayList<>(
            parteTransmissaoRepository.listarPorGuiaEPapel(guiaId, comando.papel())
        );
        UUID tenantId = TenantContext.getObrigatorio();
        ParteTransmissao nova = new ParteTransmissao(
            UUID.randomUUID(),
            tenantId,
            guiaId,
            comando.contribuinteId(),
            comando.papel(),
            comando.porcentagem(),
            comando.principal()
        );
        atuais.add(nova);
        ParteTransmissao.validarComposicaoParcial(atuais);

        return parteTransmissaoRepository.salvar(nova);
    }

    @Transactional
    public void remover(UUID guiaId, UUID parteId) {
        validarGuiaExiste(guiaId);

        ParteTransmissao existente = parteTransmissaoRepository.buscarPorId(parteId)
            .filter(p -> p.guiaId().equals(guiaId))
            .orElseThrow(() -> new NotFoundException("Parte da transmissão não encontrada para esta guia."));

        parteTransmissaoRepository.remover(existente.id());
    }

    private void validarGuiaExiste(UUID guiaId) {
        if (guiaItbiRepository.buscarPorId(guiaId).isEmpty()) {
            throw new NotFoundException("Guia ITBI não encontrada.");
        }
    }

    public record AdicionarParteTransmissaoComando(
        UUID contribuinteId,
        PapelParteTransmissao papel,
        BigDecimal porcentagem,
        boolean principal
    ) {
    }
}
