package br.com.tributos.kernel.tenancy;

import java.util.List;
import java.util.UUID;

/** Lista IDs de tenants ativos — usado por jobs agendados multi-tenant. */
public interface ListarTenantsAtivosPort {

    List<UUID> listarIds();
}
