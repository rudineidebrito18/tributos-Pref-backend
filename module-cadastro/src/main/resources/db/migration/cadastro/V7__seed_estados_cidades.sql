-- UFs brasileiras — referência para seleção de cidade em endereços.
INSERT INTO estado (sigla, nome) VALUES
    ('AC', 'Acre'),
    ('AL', 'Alagoas'),
    ('AP', 'Amapá'),
    ('AM', 'Amazonas'),
    ('BA', 'Bahia'),
    ('CE', 'Ceará'),
    ('DF', 'Distrito Federal'),
    ('ES', 'Espírito Santo'),
    ('GO', 'Goiás'),
    ('MA', 'Maranhão'),
    ('MT', 'Mato Grosso'),
    ('MS', 'Mato Grosso do Sul'),
    ('MG', 'Minas Gerais'),
    ('PA', 'Pará'),
    ('PB', 'Paraíba'),
    ('PR', 'Paraná'),
    ('PE', 'Pernambuco'),
    ('PI', 'Piauí'),
    ('RJ', 'Rio de Janeiro'),
    ('RN', 'Rio Grande do Norte'),
    ('RS', 'Rio Grande do Sul'),
    ('RO', 'Rondônia'),
    ('RR', 'Roraima'),
    ('SC', 'Santa Catarina'),
    ('SP', 'São Paulo'),
    ('SE', 'Sergipe'),
    ('TO', 'Tocantins');

-- Capitais + municípios usados em testes locais (tenant demo).
INSERT INTO cidade (estado_id, nome, codigo_ibge)
SELECT e.id, c.nome, c.codigo_ibge
FROM estado e
JOIN (VALUES
    ('SP', 'São Paulo', '3550308'),
    ('SP', 'Campinas', '3509502'),
    ('RJ', 'Rio de Janeiro', '3304557'),
    ('MG', 'Belo Horizonte', '3106200'),
    ('PR', 'Curitiba', '4106902'),
    ('RS', 'Porto Alegre', '4314902'),
    ('BA', 'Salvador', '2927408'),
    ('DF', 'Brasília', '5300108')
) AS c(uf, nome, codigo_ibge) ON e.sigla = c.uf;
