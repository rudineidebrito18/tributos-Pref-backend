package br.com.tributos.identity.adapters.out.audit;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.adapters.out.persistence.LogAuditoriaJpaEntity;
import br.com.tributos.identity.adapters.out.persistence.LogAuditoriaJpaRepository;
import br.com.tributos.identity.adapters.out.security.UsuarioAutenticadoResolver;
import br.com.tributos.kernel.audit.AuditoriaPort;
import br.com.tributos.kernel.audit.RegistroAuditoria;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class AuditoriaAdapter implements AuditoriaPort {

    private final LogAuditoriaJpaRepository logAuditoriaJpaRepository;
    private final UsuarioAutenticadoResolver usuarioAutenticadoResolver;

    public AuditoriaAdapter(
        LogAuditoriaJpaRepository logAuditoriaJpaRepository,
        UsuarioAutenticadoResolver usuarioAutenticadoResolver
    ) {
        this.logAuditoriaJpaRepository = logAuditoriaJpaRepository;
        this.usuarioAutenticadoResolver = usuarioAutenticadoResolver;
    }

    @Override
    public void registrar(RegistroAuditoria registro) {
        LogAuditoriaJpaEntity entidade = LogAuditoriaJpaEntity.nova();
        entidade.setId(UUID.randomUUID());
        entidade.setTenantId(TenantContext.getObrigatorio());
        usuarioAutenticadoResolver.usuarioIdAtual().ifPresent(entidade::setUsuarioId);
        entidade.setEntidade(registro.entidade());
        entidade.setEntidadeId(registro.entidadeId());
        entidade.setAcao(registro.acao());
        entidade.setDadosAntes(registro.dadosAntes());
        entidade.setDadosDepois(registro.dadosDepois());
        entidade.setCriadoEm(Instant.now());
        logAuditoriaJpaRepository.save(entidade);
    }
}
