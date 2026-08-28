package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.domain.TenantDominioRepository;
import br.com.tributos.identity.domain.TenantRepository;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

/**
 * Onboarding administrativo de uma nova prefeitura/câmara (tenant) — a única operação da
 * plataforma que legitimamente cria dados "fora" do tenant de quem está autenticado. Só
 * pode ser chamada por um usuário PLATAFORMA_ADMIN (ver
 * {@code adapters.in.web.TenantAdminController}, protegido por {@code @PreAuthorize}),
 * cujo próprio tenant é o tenant técnico {@code _plataforma} (ver
 * V5__tenant_dominio_e_plataforma_admin.sql) — nunca um tenant de prefeitura real.
 *
 * <p>Este método troca deliberadamente o {@link TenantContext} para o id do tenant
 * recém-gerado ANTES de delegar a persistência a {@link CriarTenantTransacional} (bean
 * separado — ver o porquê no Javadoc daquela classe): sem isso, o INSERT em
 * {@code usuario} (protegido por RLS via {@code aplicar_isolamento_tenant}) seria
 * rejeitado pela policy {@code WITH CHECK}, porque a sessão JDBC ainda estaria com
 * {@code app.current_tenant = <id do tenant _plataforma>}, vindo do JWT de quem chamou o
 * endpoint (ver {@code TenantContextFilter}). O valor anterior é restaurado no
 * {@code finally}, para não vazar para o resto da requisição.
 */
@Service
public class CriarTenantService {

    private final TenantRepository tenantRepository;
    private final TenantDominioRepository tenantDominioRepository;
    private final CriarTenantTransacional criarTenantTransacional;

    public CriarTenantService(
        TenantRepository tenantRepository,
        TenantDominioRepository tenantDominioRepository,
        CriarTenantTransacional criarTenantTransacional
    ) {
        this.tenantRepository = tenantRepository;
        this.tenantDominioRepository = tenantDominioRepository;
        this.criarTenantTransacional = criarTenantTransacional;
    }

    public TenantCriado executar(CriarTenantComando comando) {
        validarDisponibilidade(comando);

        UUID novoTenantId = UUID.randomUUID();
        UUID tenantContextAnterior = TenantContext.get();
        TenantContext.set(novoTenantId);
        try {
            return criarTenantTransacional.executar(comando, novoTenantId);
        } finally {
            if (tenantContextAnterior != null) {
                TenantContext.set(tenantContextAnterior);
            } else {
                TenantContext.clear();
            }
        }
    }

    private void validarDisponibilidade(CriarTenantComando comando) {
        if (tenantRepository.buscarPorSlug(comando.slug()).isPresent()) {
            throw new ValidationException("Já existe um tenant com o slug \"" + comando.slug() + "\".");
        }
        if (comando.dominioProprio() != null && !comando.dominioProprio().isBlank()
            && tenantDominioRepository.existePorDominio(comando.dominioProprio())) {
            throw new ValidationException("O domínio \"" + comando.dominioProprio() + "\" já está em uso por outro tenant.");
        }
    }
}
