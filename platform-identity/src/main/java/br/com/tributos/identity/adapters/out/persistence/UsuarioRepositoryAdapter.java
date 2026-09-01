package br.com.tributos.identity.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.domain.TipoMfa;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;
    private final PapelJpaRepository papelJpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository, PapelJpaRepository papelJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.papelJpaRepository = papelJpaRepository;
    }

    @Override
    public Optional<Usuario> buscarPorLogin(UUID tenantId, String login) {
        return jpaRepository.findByTenantIdAndLogin(tenantId, login).map(UsuarioRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(UUID tenantId, String email) {
        return jpaRepository.findByTenantIdAndEmail(tenantId, email).map(UsuarioRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(UsuarioRepositoryAdapter::paraDominio);
    }

    @Override
    public void salvar(Usuario usuario) {
        UsuarioJpaEntity entidade = jpaRepository.findById(usuario.getId())
            .orElseGet(() -> new UsuarioJpaEntity(
                usuario.getId(), usuario.getTenantId(), usuario.getNome(), usuario.getLogin(), usuario.getEmail(),
                usuario.getFotoDocumentoId(), usuario.getSenhaHash(), usuario.isMfaHabilitado(),
                usuario.getMfaTipo(), usuario.getMfaSecret(), usuario.getMfaCodigoExpiraEm(), usuario.isAtivo()
            ));

        entidade.setNome(usuario.getNome());
        entidade.setLogin(usuario.getLogin());
        entidade.setEmail(usuario.getEmail());
        entidade.setFotoDocumentoId(usuario.getFotoDocumentoId());
        entidade.setSenhaHash(usuario.getSenhaHash());
        entidade.setMfaHabilitado(usuario.isMfaHabilitado());
        entidade.setMfaTipo(usuario.getMfaTipo());
        entidade.setMfaSecret(usuario.getMfaSecret());
        entidade.setMfaCodigoExpiraEm(usuario.getMfaCodigoExpiraEm());

        jpaRepository.save(entidade);
    }

    @Override
    public Set<String> buscarNomesDosPapeis(UUID usuarioId) {
        return jpaRepository.findById(usuarioId)
            .map(entidade -> entidade.getPapeis().stream().map(PapelJpaEntity::getNome).collect(Collectors.toSet()))
            .orElseGet(Set::of);
    }

    @Override
    public List<Usuario> listarAtivosDoTenant(UUID tenantId) {
        return jpaRepository.findByTenantIdAndAtivoTrueOrderByLoginAsc(tenantId).stream()
            .map(UsuarioRepositoryAdapter::paraDominio)
            .toList();
    }

    @Override
    public boolean existeLoginOuEmail(UUID tenantId, String login, String email, UUID excluirUsuarioId) {
        return jpaRepository.existsLoginOuEmail(tenantId, login, email, excluirUsuarioId);
    }

    @Override
    public void atribuirPapel(UUID usuarioId, String nomeDoPapel) {
        UsuarioJpaEntity usuario = jpaRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalStateException("Usuário " + usuarioId + " não encontrado para atribuir papel."));
        PapelJpaEntity papel = papelJpaRepository.findByNome(nomeDoPapel)
            .orElseThrow(() -> new IllegalStateException("Papel \"" + nomeDoPapel + "\" não cadastrado no catálogo."));

        usuario.getPapeis().add(papel);
        jpaRepository.save(usuario);
    }

    private static Usuario paraDominio(UsuarioJpaEntity entidade) {
        TipoMfa tipoMfa = entidade.getMfaTipo() == null ? TipoMfa.NENHUM : entidade.getMfaTipo();
        return new Usuario(
            entidade.getId(), entidade.getTenantId(), entidade.getNome(), entidade.getLogin(), entidade.getEmail(),
            entidade.getFotoDocumentoId(), entidade.getSenhaHash(), entidade.isMfaHabilitado(),
            tipoMfa, entidade.getMfaSecret(), entidade.getMfaCodigoExpiraEm(), entidade.isAtivo()
        );
    }
}
