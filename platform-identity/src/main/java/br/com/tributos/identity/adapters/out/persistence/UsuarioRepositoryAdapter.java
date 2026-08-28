package br.com.tributos.identity.adapters.out.persistence;

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

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> buscarPorLogin(UUID tenantId, String login) {
        return jpaRepository.findByTenantIdAndLogin(tenantId, login).map(UsuarioRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(UsuarioRepositoryAdapter::paraDominio);
    }

    @Override
    public void salvar(Usuario usuario) {
        UsuarioJpaEntity entidade = jpaRepository.findById(usuario.getId())
            .orElseGet(() -> new UsuarioJpaEntity(
                usuario.getId(), usuario.getTenantId(), usuario.getLogin(), usuario.getEmail(),
                usuario.getSenhaHash(), usuario.isMfaHabilitado(), usuario.getMfaTipo(), usuario.getMfaSecret(), usuario.isAtivo()
            ));

        entidade.setSenhaHash(usuario.getSenhaHash());
        entidade.setMfaHabilitado(usuario.isMfaHabilitado());
        entidade.setMfaTipo(usuario.getMfaTipo());
        entidade.setMfaSecret(usuario.getMfaSecret());

        jpaRepository.save(entidade);
    }

    @Override
    public Set<String> buscarNomesDosPapeis(UUID usuarioId) {
        return jpaRepository.findById(usuarioId)
            .map(entidade -> entidade.getPapeis().stream().map(PapelJpaEntity::getNome).collect(Collectors.toSet()))
            .orElseGet(Set::of);
    }

    private static Usuario paraDominio(UsuarioJpaEntity entidade) {
        TipoMfa tipoMfa = entidade.getMfaTipo() == null ? TipoMfa.NENHUM : entidade.getMfaTipo();
        return new Usuario(
            entidade.getId(), entidade.getTenantId(), entidade.getLogin(), entidade.getEmail(),
            entidade.getSenhaHash(), entidade.isMfaHabilitado(), tipoMfa, entidade.getMfaSecret(), entidade.isAtivo()
        );
    }
}
