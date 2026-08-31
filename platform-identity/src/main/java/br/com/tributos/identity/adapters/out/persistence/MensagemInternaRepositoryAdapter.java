package br.com.tributos.identity.adapters.out.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.identity.domain.CaixaMensagem;
import br.com.tributos.identity.domain.MensagemInterna;
import br.com.tributos.identity.domain.MensagemInternaDestinatario;
import br.com.tributos.identity.domain.MensagemInternaRepository;

@Component
public class MensagemInternaRepositoryAdapter implements MensagemInternaRepository {

    private final MensagemInternaJpaRepository mensagemJpaRepository;
    private final MensagemInternaDestinatarioJpaRepository destinatarioJpaRepository;

    public MensagemInternaRepositoryAdapter(
        MensagemInternaJpaRepository mensagemJpaRepository,
        MensagemInternaDestinatarioJpaRepository destinatarioJpaRepository
    ) {
        this.mensagemJpaRepository = mensagemJpaRepository;
        this.destinatarioJpaRepository = destinatarioJpaRepository;
    }

    @Override
    public MensagemInterna salvar(MensagemInterna mensagem) {
        MensagemInternaJpaEntity entidade = mensagemJpaRepository.findById(mensagem.getId())
            .orElseGet(MensagemInternaJpaEntity::new);

        entidade.setId(mensagem.getId());
        entidade.setTenantId(mensagem.getTenantId());
        entidade.setRemetenteId(mensagem.getRemetenteId());
        entidade.setAssunto(mensagem.getAssunto());
        entidade.setCorpo(mensagem.getCorpo());
        if (mensagem.getCriadoEm() != null) {
            entidade.setCriadoEm(mensagem.getCriadoEm());
        }

        entidade.getDestinatarios().clear();
        for (MensagemInternaDestinatario destinatario : mensagem.getDestinatarios()) {
            MensagemInternaDestinatarioJpaEntity destEntidade = new MensagemInternaDestinatarioJpaEntity();
            destEntidade.setId(destinatario.getId());
            destEntidade.setTenantId(destinatario.getTenantId());
            destEntidade.setMensagem(entidade);
            destEntidade.setDestinatarioId(destinatario.getDestinatarioId());
            destEntidade.setLidaEm(destinatario.getLidaEm());
            destEntidade.setArquivadaEm(destinatario.getArquivadaEm());
            entidade.getDestinatarios().add(destEntidade);
        }

        return paraDominio(mensagemJpaRepository.save(entidade));
    }

    @Override
    public Optional<MensagemInterna> buscarPorId(UUID id) {
        return mensagemJpaRepository.buscarComDestinatarios(id).map(MensagemInternaRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<MensagemInterna> listarCaixa(
        UUID usuarioId,
        CaixaMensagem caixa,
        String assuntoPattern,
        String corpoPattern,
        Pageable pageable
    ) {
        Page<MensagemInternaJpaEntity> pagina = switch (caixa) {
            case ENTRADA -> mensagemJpaRepository.listarEntrada(usuarioId, assuntoPattern, corpoPattern, pageable);
            case ENVIADAS -> mensagemJpaRepository.listarEnviadas(usuarioId, assuntoPattern, corpoPattern, pageable);
            case ARQUIVADAS -> mensagemJpaRepository.listarArquivadas(usuarioId, assuntoPattern, corpoPattern, pageable);
        };

        List<UUID> ids = pagina.getContent().stream().map(MensagemInternaJpaEntity::getId).toList();
        Map<UUID, MensagemInternaJpaEntity> comDestinatarios = ids.isEmpty()
            ? Map.of()
            : mensagemJpaRepository.buscarComDestinatariosPorIds(ids).stream()
                .collect(Collectors.toMap(MensagemInternaJpaEntity::getId, Function.identity()));

        return pagina.map(entidade -> paraDominio(comDestinatarios.getOrDefault(entidade.getId(), entidade)));
    }

    @Override
    public Optional<MensagemInternaDestinatario> buscarDestinatario(UUID mensagemId, UUID destinatarioId) {
        return destinatarioJpaRepository.findByMensagem_IdAndDestinatarioId(mensagemId, destinatarioId)
            .map(MensagemInternaRepositoryAdapter::paraDominioDestinatario);
    }

    @Override
    public void salvarDestinatario(MensagemInternaDestinatario destinatario) {
        MensagemInternaDestinatarioJpaEntity entidade = destinatarioJpaRepository.findById(destinatario.getId())
            .orElseThrow(() -> new IllegalStateException("Destinatário da mensagem não encontrado: " + destinatario.getId()));

        entidade.setLidaEm(destinatario.getLidaEm());
        entidade.setArquivadaEm(destinatario.getArquivadaEm());
        destinatarioJpaRepository.save(entidade);
    }

    private static MensagemInterna paraDominio(MensagemInternaJpaEntity entidade) {
        MensagemInterna mensagem = new MensagemInterna();
        mensagem.setId(entidade.getId());
        mensagem.setTenantId(entidade.getTenantId());
        mensagem.setRemetenteId(entidade.getRemetenteId());
        mensagem.setAssunto(entidade.getAssunto());
        mensagem.setCorpo(entidade.getCorpo());
        mensagem.setCriadoEm(entidade.getCriadoEm());

        List<MensagemInternaDestinatario> destinatarios = entidade.getDestinatarios().stream()
            .map(MensagemInternaRepositoryAdapter::paraDominioDestinatario)
            .toList();
        mensagem.setDestinatarios(destinatarios);
        return mensagem;
    }

    private static MensagemInternaDestinatario paraDominioDestinatario(MensagemInternaDestinatarioJpaEntity entidade) {
        MensagemInternaDestinatario destinatario = new MensagemInternaDestinatario();
        destinatario.setId(entidade.getId());
        destinatario.setTenantId(entidade.getTenantId());
        destinatario.setMensagemId(entidade.getMensagem().getId());
        destinatario.setDestinatarioId(entidade.getDestinatarioId());
        destinatario.setLidaEm(entidade.getLidaEm());
        destinatario.setArquivadaEm(entidade.getArquivadaEm());
        return destinatario;
    }
}
