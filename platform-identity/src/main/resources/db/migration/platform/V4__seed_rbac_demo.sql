-- Papéis-base (RBAC) — ver PLANEJAMENTO_PROJETO.md §9. Globais (tenant_id NULL): o
-- CONJUNTO de papéis disponíveis é o mesmo em toda prefeitura; o que varia por tenant é
-- QUEM tem qual papel (tabela usuario_papel), não a existência do papel em si.
INSERT INTO papel (id, tenant_id, nome, descricao) VALUES
    (gen_random_uuid(), NULL, 'ADMIN_TENANT', 'Administrador da prefeitura/câmara — acesso total dentro do próprio tenant'),
    (gen_random_uuid(), NULL, 'FISCAL', 'Fiscal tributário — lançamentos, autuações e consultas de contribuinte'),
    (gen_random_uuid(), NULL, 'ATENDENTE', 'Atendimento ao contribuinte — emissão de guias e certidões, sem alçada de alteração de alíquota');

-- Catálogo de permissões (código no formato modulo:recurso:acao) — granularidade fina o
-- suficiente para o PermissionEvaluator (ABAC) de uma fase futura compor regras por
-- atributo além do papel puro, sem precisar alterar esta tabela.
INSERT INTO permissao (id, codigo, descricao, modulo) VALUES
    (gen_random_uuid(), 'plataforma:usuarios:gerenciar', 'Criar/editar/desativar usuários do tenant', 'plataforma'),
    (gen_random_uuid(), 'plataforma:papeis:gerenciar', 'Atribuir papéis a usuários', 'plataforma'),
    (gen_random_uuid(), 'cadastro:contribuinte:escrever', 'Criar/editar contribuintes e imóveis', 'cadastro'),
    (gen_random_uuid(), 'cadastro:contribuinte:ler', 'Consultar contribuintes e imóveis', 'cadastro');

INSERT INTO papel_permissao (papel_id, permissao_id)
SELECT p.id, perm.id
FROM papel p, permissao perm
WHERE p.nome = 'ADMIN_TENANT'; -- admin acumula todas as permissões do catálogo atual

INSERT INTO papel_permissao (papel_id, permissao_id)
SELECT p.id, perm.id
FROM papel p, permissao perm
WHERE p.nome = 'ATENDENTE' AND perm.codigo IN ('cadastro:contribuinte:ler', 'cadastro:contribuinte:escrever');

-- Usuário administrador do tenant "demo" para desenvolvimento local. Senha: "Demo@123"
-- (hash BCrypt abaixo). USE SOMENTE EM AMBIENTE LOCAL — ver README.md "Segurança —
-- pendências conhecidas do Sprint 0".
INSERT INTO usuario (id, tenant_id, login, email, senha_hash, mfa_habilitado, ativo)
SELECT gen_random_uuid(), t.id, 'admin', 'admin@demo.gov.br',
       '$2a$10$sSqLb.JalB61zOLrMb/9wuqqEdjKQgkhuCqYZmhuSDizy0hCd3S7K', false, true
FROM tenant t
WHERE t.slug = 'demo';

INSERT INTO usuario_papel (usuario_id, papel_id)
SELECT u.id, p.id
FROM usuario u, papel p
WHERE u.login = 'admin' AND p.nome = 'ADMIN_TENANT';
