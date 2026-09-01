package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.application.CalculadorHabitese.ResultadoCalculo;
import br.com.tributos.iptu.domain.ContribuinteReferenciaRepository;
import br.com.tributos.iptu.domain.EmitirHabiteseComando;
import br.com.tributos.iptu.domain.GeradorCodigoVerificacao;
import br.com.tributos.iptu.domain.HabiteseImovel;
import br.com.tributos.iptu.domain.HabiteseImovelRepository;
import br.com.tributos.iptu.domain.HabiteseResponsavel;
import br.com.tributos.iptu.domain.ImovelHabiteseTipo;
import br.com.tributos.iptu.domain.ImovelHabiteseTipoRepository;
import br.com.tributos.iptu.domain.ImovelRepository;
import br.com.tributos.iptu.domain.SituacaoFiscalHabitese;
import br.com.tributos.kernel.events.HabiteseEmitidoEvent;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class EmitirHabiteseService {

    private final HabiteseImovelRepository habiteseImovelRepository;
    private final ImovelRepository imovelRepository;
    private final ImovelHabiteseTipoRepository habiteseTipoRepository;
    private final ContribuinteReferenciaRepository contribuinteReferenciaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EmitirHabiteseService(
        HabiteseImovelRepository habiteseImovelRepository,
        ImovelRepository imovelRepository,
        ImovelHabiteseTipoRepository habiteseTipoRepository,
        ContribuinteReferenciaRepository contribuinteReferenciaRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.habiteseImovelRepository = habiteseImovelRepository;
        this.imovelRepository = imovelRepository;
        this.habiteseTipoRepository = habiteseTipoRepository;
        this.contribuinteReferenciaRepository = contribuinteReferenciaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public HabiteseImovel executar(UUID imovelId, EmitirHabiteseComando comando) {
        if (!imovelRepository.buscarPorId(imovelId).isPresent()) {
            throw new NotFoundException("Imóvel não encontrado.");
        }

        ImovelHabiteseTipo tipo = habiteseTipoRepository.buscarPorId(comando.tipoId())
            .orElseThrow(() -> new ValidationException("Informe um tipo de habite-se válido."));

        if (!tipo.ativo()) {
            throw new ValidationException("O tipo de habite-se selecionado está inativo.");
        }

        validarCamposObrigatorios(comando);

        if (!contribuinteReferenciaRepository.existe(comando.contribuinteId())) {
            throw new ValidationException("Contribuinte não encontrado.");
        }

        UUID contribuintePessoaId = contribuinteReferenciaRepository.buscarPessoaId(comando.contribuinteId())
            .orElseThrow(() -> new ValidationException("Contribuinte não encontrado."));

        ResultadoCalculo calculo = CalculadorHabitese.calcular(
            tipo,
            comando.areaImovel(),
            comando.valorBaseCalculo(),
            comando.desconto()
        );

        UUID tenantId = TenantContext.getObrigatorio();
        long numero = habiteseImovelRepository.proximoNumero();
        String codigoVerificacao = gerarCodigoUnico();
        Instant dataEmissaoTs = Instant.now();
        BigDecimal desconto = comando.desconto() != null ? comando.desconto() : BigDecimal.ZERO;
        SituacaoFiscalHabitese situacaoFiscal = calculo.valor().compareTo(BigDecimal.ZERO) > 0
            ? SituacaoFiscalHabitese.PENDENTE
            : SituacaoFiscalHabitese.ISENTA;

        HabiteseImovel habitese = new HabiteseImovel(
            UUID.randomUUID(),
            tenantId,
            imovelId,
            comando.tipoId(),
            numero,
            comando.dataEmissao(),
            dataEmissaoTs,
            comando.ano(),
            comando.validade(),
            comando.contribuinteId(),
            comando.areaImovel(),
            comando.dataConclusao(),
            comando.numeroAlvara(),
            comando.dataAlvara(),
            comando.validadeAlvara(),
            comando.valorBaseCalculo(),
            calculo.baseCalculo(),
            desconto,
            calculo.valor(),
            comando.frente(),
            comando.fundos(),
            comando.ladoEsquerdo(),
            comando.ladoDireito(),
            comando.observacao(),
            codigoVerificacao,
            situacaoFiscal,
            montarResponsaveis(comando.responsaveis())
        );

        HabiteseImovel salvo = habiteseImovelRepository.salvar(habitese);

        if (calculo.valor().compareTo(BigDecimal.ZERO) > 0) {
            eventPublisher.publishEvent(new HabiteseEmitidoEvent(
                salvo.id(),
                tenantId,
                contribuintePessoaId,
                calculo.valor(),
                dataEmissaoTs
            ));
        }

        return salvo;
    }

    private static void validarCamposObrigatorios(EmitirHabiteseComando comando) {
        if (comando.dataEmissao() == null) {
            throw new ValidationException("Informe a data de emissão do habite-se.");
        }
        if (comando.ano() == null) {
            throw new ValidationException("Informe o ano do habite-se.");
        }
        if (comando.contribuinteId() == null) {
            throw new ValidationException("Informe o contribuinte do habite-se.");
        }
        if (comando.areaImovel() == null) {
            throw new ValidationException("Informe a área do imóvel.");
        }
        if (comando.dataConclusao() == null) {
            throw new ValidationException("Informe a data de conclusão da obra.");
        }
        if (comando.valorBaseCalculo() == null) {
            throw new ValidationException("Informe o valor base de cálculo.");
        }
    }

    private List<HabiteseResponsavel> montarResponsaveis(List<EmitirHabiteseComando.ResponsavelComando> responsaveis) {
        if (responsaveis == null || responsaveis.isEmpty()) {
            return List.of();
        }
        if (responsaveis.size() > 2) {
            throw new ValidationException("Informe no máximo dois responsáveis técnicos.");
        }

        List<HabiteseResponsavel> resultado = new ArrayList<>();
        short ordem = 1;
        for (EmitirHabiteseComando.ResponsavelComando responsavel : responsaveis) {
            if (responsavel.nome() == null || responsavel.nome().isBlank()) {
                throw new ValidationException("Informe o nome do responsável técnico " + ordem + ".");
            }
            resultado.add(new HabiteseResponsavel(
                UUID.randomUUID(),
                ordem,
                responsavel.nome().trim(),
                responsavel.profissao(),
                responsavel.documento()
            ));
            ordem++;
        }
        return resultado;
    }

    private String gerarCodigoUnico() {
        for (int tentativa = 0; tentativa < 5; tentativa++) {
            String codigo = GeradorCodigoVerificacao.gerarLegado();
            if (!habiteseImovelRepository.existeCodigoVerificacao(codigo)) {
                return codigo;
            }
        }
        throw new IllegalStateException("Não foi possível gerar código de verificação único para o habite-se.");
    }
}
