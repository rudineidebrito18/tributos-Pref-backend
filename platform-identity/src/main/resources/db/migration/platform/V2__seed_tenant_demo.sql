-- Tenant de demonstração para desenvolvimento local — mesmo slug usado como fallback pelo
-- frontend (NEXT_PUBLIC_DEFAULT_TENANT_SLUG, ver frontend/.env.example). Permite validar o
-- fluxo de branding ponta a ponta (middleware -> layout.tsx -> este endpoint) sem precisar
-- cadastrar uma prefeitura real primeiro.
INSERT INTO tenant (
    slug, nome, uf, tipo_entidade, logo_url,
    cor_accent, cor_accent_dark, cor_accent_secondary, cor_accent_tertiary, ativo
) VALUES (
    'demo', 'Município Demonstração', 'BR', 'PREFEITURA', '/logo-placeholder.svg',
    '#4c8dff', '#2e6bdb', '#ff5d72', '#34d399', true
);

INSERT INTO tenant_modulo_ativo (tenant_id, modulo_id)
SELECT id, modulo
FROM tenant, unnest(ARRAY['cadastro', 'iss', 'iptu', 'itbi', 'financeiro']) AS modulo
WHERE slug = 'demo';
