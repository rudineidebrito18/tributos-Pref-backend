package br.com.tributos.identity.application;

import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.identity.domain.PaletaTenant;
import br.com.tributos.identity.domain.Tenant;
import br.com.tributos.identity.domain.TenantDominio;
import br.com.tributos.identity.domain.TenantDominioRepository;
import br.com.tributos.identity.domain.TenantRepository;
import br.com.tributos.identity.domain.TipoMfa;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;

/**
 * Bean SEPARADO de {@link CriarTenantService} de propósito — não é só organização de
 * código. O interceptor de {@code @Transactional} do Spring inicia a transação (e, com
 * ela, a conexão JDBC onde o {@code TenantAwareDataSource} executa
 * {@code SET LOCAL app.current_tenant}) no momento em que ESTE bean é invocado através do
 * seu proxy — ou seja, {@link CriarTenantService} precisa ter terminado de chamar
 * {@code TenantContext.set(novoTenantId)} ANTES de invocar este método. Se a anotação
 * {@code @Transactional} estivesse no próprio {@code executar()} de
 * {@code CriarTenantService}, a transação começaria antes do corpo do método rodar,
 * usando ainda o tenant do chamador (a plataforma) — e o INSERT em {@code usuario}
 * (protegido por RLS) seria rejeitado pela policy {@code WITH CHECK}.
 */
@Service
class CriarTenantTransacional {

    private static final String PAPEL_ADMIN_TENANT = "ADMIN_TENANT";

    private final TenantRepository tenantRepository;
    private final TenantDominioRepository tenantDominioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    CriarTenantTransacional(
        TenantRepository tenantRepository,
        TenantDominioRepository tenantDominioRepository,
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.tenantRepository = tenantRepository;
        this.tenantDominioRepository = tenantDominioRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    TenantCriado executar(CriarTenantComando comando, UUID novoTenantId) {
        tenantRepository.salvar(new Tenant(
            novoTenantId,
            comando.slug(),
            comando.nome(),
            comando.uf(),
            comando.tipoEntidade(),
            comando.logoUrl(),
            comando.paleta() != null ? comando.paleta() : PaletaTenant.padrao(),
            comando.modulosAtivos() != null ? comando.modulosAtivos() : Set.of(),
            true
        ));

        if (comando.dominioProprio() != null && !comando.dominioProprio().isBlank()) {
            tenantDominioRepository.salvar(TenantDominio.novo(novoTenantId, comando.dominioProprio()));
        }

        String senhaTemporaria = SenhaTemporariaFactory.gerar();
        UUID usuarioAdminId = UUID.randomUUID();
        usuarioRepository.salvar(new Usuario(
            usuarioAdminId, novoTenantId, comando.loginAdminInicial(), comando.emailAdminInicial(),
            passwordEncoder.encode(senhaTemporaria), false, TipoMfa.NENHUM, null, true
        ));
        usuarioRepository.atribuirPapel(usuarioAdminId, PAPEL_ADMIN_TENANT);

        return new TenantCriado(novoTenantId, comando.slug(), usuarioAdminId, comando.loginAdminInicial(), senhaTemporaria);
    }
}
