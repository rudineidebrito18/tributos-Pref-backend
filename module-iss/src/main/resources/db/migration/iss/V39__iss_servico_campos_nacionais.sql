ALTER TABLE iss_servico ADD COLUMN grupo_servico_id UUID REFERENCES iss_grupo_servico(id);
ALTER TABLE iss_servico ADD COLUMN codigo_nbs VARCHAR(20);
ALTER TABLE iss_servico ADD COLUMN codigo_tributacao_nacional VARCHAR(20);
ALTER TABLE iss_servico ADD COLUMN indop VARCHAR(10);
ALTER TABLE iss_servico ADD COLUMN c_class_trib VARCHAR(10);

UPDATE iss_servico s
SET grupo_servico_id = g.id
FROM iss_grupo_servico g
WHERE s.tenant_id = g.tenant_id
  AND g.codigo = split_part(s.codigo_lc116, '.', 1)
  AND s.grupo_servico_id IS NULL;
