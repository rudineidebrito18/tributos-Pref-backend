-- Seed Sprint 3: serviços LC 116 (amostra), atividades e faixas Anexo III Simples Nacional 2024.

DO $$
DECLARE
    v_tenant_id UUID;
    v_regime_simples UUID := 'd0000001-0000-4000-8000-000000000001';
    v_atividade_id UUID := 'e0000001-0000-4000-8000-000000000001';
    v_servico_1401 UUID := 'e0000002-0000-4000-8000-000000000001';
    v_servico_1701 UUID := 'e0000002-0000-4000-8000-000000000002';
    v_competencia DATE := '2024-01-01';
BEGIN
    SELECT id INTO v_tenant_id FROM tenant WHERE slug = 'demo';

    INSERT INTO iss_atividade (id, tenant_id, codigo, descricao) VALUES
        (v_atividade_id, v_tenant_id, '6201-5/00', 'Desenvolvimento de programas de computador sob encomenda')
    ON CONFLICT DO NOTHING;

    INSERT INTO iss_servico (id, tenant_id, codigo_lc116, descricao, aliquota_minima, aliquota_maxima) VALUES
        (v_servico_1401, v_tenant_id, '14.01', 'Lubrificação, limpeza, lustração, revisão, carga e recarga, conserto, restauração, blindagem, manutenção e conservação de máquinas, veículos, aparelhos, equipamentos, motores, elevadores ou de qualquer objeto (exceto peças e partes empregadas, que ficam sujeitas ao ICMS).', 2.0, 5.0),
        (v_servico_1701, v_tenant_id, '17.01', 'Assessoria ou consultoria de qualquer natureza, não contida em outros itens desta lista; análise, exame, pesquisa, coleta, compilação e fornecimento de dados e informações de qualquer natureza, inclusive cadastro e similares.', 2.0, 5.0)
    ON CONFLICT DO NOTHING;

    -- Anexo III — faixas oficiais Simples Nacional (serviços), vigência 2024.
    INSERT INTO iss_aliquota_regime (id, tenant_id, regime_id, faixa_receita_min, faixa_receita_max, aliquota_nominal, parcela_deduzir, percentual_iss, competencia_vigencia, anexo_simples) VALUES
        ('f0000001-0000-4000-8000-000000000001', v_tenant_id, v_regime_simples, 0, 180000, 6.0000, 0, 33.5000, v_competencia, 'III'),
        ('f0000001-0000-4000-8000-000000000002', v_tenant_id, v_regime_simples, 180000.01, 360000, 11.2000, 9360, 33.5000, v_competencia, 'III'),
        ('f0000001-0000-4000-8000-000000000003', v_tenant_id, v_regime_simples, 360000.01, 720000, 13.5000, 17640, 33.5000, v_competencia, 'III'),
        ('f0000001-0000-4000-8000-000000000004', v_tenant_id, v_regime_simples, 720000.01, 1800000, 16.0000, 35640, 33.5000, v_competencia, 'III'),
        ('f0000001-0000-4000-8000-000000000005', v_tenant_id, v_regime_simples, 1800000.01, 3600000, 21.0000, 125640, 33.5000, v_competencia, 'III'),
        ('f0000001-0000-4000-8000-000000000006', v_tenant_id, v_regime_simples, 3600000.01, 4800000, 33.0000, 648000, 33.5000, v_competencia, 'III')
    ON CONFLICT DO NOTHING;
END $$;
