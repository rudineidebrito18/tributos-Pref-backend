-- ============================================================================
-- Cadastro de Tenant (onboarding administrativo) — ver PLANEJAMENTO_PROJETO.md §8.1
-- (decisão 2026-08-28) e ROADMAP_DESENVOLVIMENTO.md, Sprint 0.
-- ============================================================================

-- Domínio próprio por tenant (ex. tributos.prefeitura.gov.br), alternativa ao subdomínio
-- padrão. Sem RLS de propósito — mesmo motivo de `tenant`: precisa ser consultável antes
-- de qualquer autenticação (resolução de tenant pelo Host da requisição). Verificação de
-- propriedade do domínio (DNS TXT/CNAME) é manual nesta fase — `verificado` só documenta
-- intenção, não é aplicado em nenhuma regra ainda.
CREATE TABLE tenant_dominio (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
    dominio    VARCHAR(255) NOT NULL UNIQUE,
    verificado BOOLEAN NOT NULL DEFAULT false,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Papel global (mesmo padrão de ADMIN_TENANT/FISCAL/ATENDENTE em V4): cadastrar um tenant
-- novo é uma operação da PLATAFORMA, fora do escopo de qualquer prefeitura específica —
-- por isso o usuário que a executa não pode ser um ADMIN_TENANT comum (que só tem
-- permissão dentro do próprio tenant via RLS).
INSERT INTO papel (id, tenant_id, nome, descricao) VALUES
    (gen_random_uuid(), NULL, 'PLATAFORMA_ADMIN', 'Equipe da plataforma — cadastra novas prefeituras/câmaras (tenants), fora do escopo de qualquer tenant específico');

INSERT INTO permissao (id, codigo, descricao, modulo) VALUES
    (gen_random_uuid(), 'plataforma:tenants:gerenciar', 'Cadastrar e administrar tenants (prefeituras/câmaras) na plataforma', 'plataforma');

INSERT INTO papel_permissao (papel_id, permissao_id)
SELECT p.id, perm.id
FROM papel p, permissao perm
WHERE p.nome = 'PLATAFORMA_ADMIN' AND perm.codigo = 'plataforma:tenants:gerenciar';

-- "Tenant" técnico só para os usuários PLATAFORMA_ADMIN existirem (a coluna
-- usuario.tenant_id é NOT NULL — todo usuário, inclusive da equipe da plataforma, precisa
-- de uma linha em `tenant` para a FK). Slug com underscore de propósito: não colide com
-- slugs de prefeituras reais (validados como alfanuméricos+hífen na aplicação) e nunca é
-- resolvido por subdomínio/domínio público real. tipo_entidade='PREFEITURA' é só para
-- satisfazer o CHECK da coluna — este tenant nunca aparece em branding nem em telas de
-- prefeitura.
INSERT INTO tenant (slug, nome, uf, tipo_entidade, ativo) VALUES
    ('_plataforma', 'Plataforma Tributos (interno)', 'BR', 'PREFEITURA', true);

-- Usuário PLATAFORMA_ADMIN de bootstrap para desenvolvimento local. MESMA senha e MESMO
-- hash BCrypt já usados pelo usuário "admin" do tenant demo em V4 ("Demo@123") — não é um
-- segredo novo, é o mesmo dado de dev já versionado neste repositório, reaproveitado para
-- não introduzir mais uma credencial. USE SOMENTE EM AMBIENTE LOCAL (ver README.md).
INSERT INTO usuario (id, tenant_id, login, email, senha_hash, mfa_habilitado, ativo)
SELECT gen_random_uuid(), t.id, 'plataforma-admin', 'plataforma-admin@tributos.local',
       '$2a$10$sSqLb.JalB61zOLrMb/9wuqqEdjKQgkhuCqYZmhuSDizy0hCd3S7K', false, true
FROM tenant t
WHERE t.slug = '_plataforma';

INSERT INTO usuario_papel (usuario_id, papel_id)
SELECT u.id, p.id
FROM usuario u, papel p
WHERE u.login = 'plataforma-admin' AND p.nome = 'PLATAFORMA_ADMIN';
