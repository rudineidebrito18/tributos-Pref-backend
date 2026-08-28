-- Catálogos ISSQN iniciais para o tenant demo (Sprint 2).
-- Os UUIDs fixos facilitam testes de integração; em produção novos tenants recebem
-- os mesmos valores via CriarTenantService (futuro) ou script de onboarding.

DO $$
DECLARE
    v_tenant_id UUID;
    v_nao_credenciado UUID := 'a0000001-0000-4000-8000-000000000001';
    v_em_analise UUID := 'a0000001-0000-4000-8000-000000000002';
    v_aprovado UUID := 'a0000001-0000-4000-8000-000000000003';
    v_negado UUID := 'a0000001-0000-4000-8000-000000000004';
    v_cancelado UUID := 'a0000001-0000-4000-8000-000000000005';
BEGIN
    SELECT id INTO v_tenant_id FROM tenant WHERE slug = 'demo';

    INSERT INTO iss_tipo_contribuinte (id, tenant_id, nome) VALUES
        ('b0000001-0000-4000-8000-000000000001', v_tenant_id, 'Prestador de serviços'),
        ('b0000001-0000-4000-8000-000000000002', v_tenant_id, 'Construção civil'),
        ('b0000001-0000-4000-8000-000000000003', v_tenant_id, 'Autônomo')
    ON CONFLICT DO NOTHING;

    INSERT INTO iss_situacao_cadastral (id, tenant_id, nome) VALUES
        ('c0000001-0000-4000-8000-000000000001', v_tenant_id, 'ATIVA'),
        ('c0000001-0000-4000-8000-000000000002', v_tenant_id, 'INATIVA'),
        ('c0000001-0000-4000-8000-000000000003', v_tenant_id, 'SUSPENSA')
    ON CONFLICT DO NOTHING;

    INSERT INTO iss_status_credenciamento (id, tenant_id, nome) VALUES
        (v_nao_credenciado, v_tenant_id, 'NAO_CREDENCIADO'),
        (v_em_analise, v_tenant_id, 'EM_ANALISE'),
        (v_aprovado, v_tenant_id, 'APROVADO'),
        (v_negado, v_tenant_id, 'NEGADO'),
        (v_cancelado, v_tenant_id, 'CANCELADO')
    ON CONFLICT DO NOTHING;

    INSERT INTO iss_regime_tributario (id, tenant_id, nome) VALUES
        ('d0000001-0000-4000-8000-000000000001', v_tenant_id, 'SIMPLES_NACIONAL'),
        ('d0000001-0000-4000-8000-000000000002', v_tenant_id, 'MEI'),
        ('d0000001-0000-4000-8000-000000000003', v_tenant_id, 'LUCRO_PRESUMIDO'),
        ('d0000001-0000-4000-8000-000000000004', v_tenant_id, 'LUCRO_REAL'),
        ('d0000001-0000-4000-8000-000000000005', v_tenant_id, 'PESSOA_FISICA')
    ON CONFLICT DO NOTHING;
END $$;
