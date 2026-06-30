INSERT INTO perfis (id, codigo, nome, descricao)
VALUES
    ('00000000-0000-0000-0000-000000000101', 'MATRIZ_ADMIN', 'Administrador da Matriz', 'Acesso administrativo total'),
    ('00000000-0000-0000-0000-000000000102', 'MATRIZ_OPERADOR', 'Operador da Matriz', 'Operacao da matriz com acesso amplo'),
    ('00000000-0000-0000-0000-000000000103', 'FILIAL_PROFESSOR', 'Professor de Filial', 'Professor com acesso a propria filial'),
    ('00000000-0000-0000-0000-000000000104', 'FILIAL_RESPONSAVEL', 'Responsavel de Filial', 'Responsavel com acesso a propria filial')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO permissoes (id, codigo, descricao)
VALUES
    ('00000000-0000-0000-0000-000000000201', 'FILIADO_CRIAR', 'Criar filiados'),
    ('00000000-0000-0000-0000-000000000202', 'FILIADO_EDITAR', 'Editar filiados'),
    ('00000000-0000-0000-0000-000000000203', 'FILIADO_VISUALIZAR', 'Visualizar filiados'),
    ('00000000-0000-0000-0000-000000000204', 'FILIADO_INATIVAR', 'Inativar filiados'),
    ('00000000-0000-0000-0000-000000000205', 'FILIAL_CRIAR', 'Criar filiais'),
    ('00000000-0000-0000-0000-000000000206', 'FILIAL_EDITAR', 'Editar filiais'),
    ('00000000-0000-0000-0000-000000000207', 'FILIAL_VISUALIZAR_TODAS', 'Visualizar todas as filiais'),
    ('00000000-0000-0000-0000-000000000208', 'RELATORIO_VISUALIZAR_TODOS', 'Visualizar relatorios globais'),
    ('00000000-0000-0000-0000-000000000209', 'RELATORIO_VISUALIZAR_FILIAL', 'Visualizar relatorios da propria filial')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO perfis_permissoes (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM perfis p
CROSS JOIN permissoes pe
WHERE p.codigo = 'MATRIZ_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO perfis_permissoes (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM perfis p
JOIN permissoes pe ON pe.codigo IN (
    'FILIADO_CRIAR',
    'FILIADO_EDITAR',
    'FILIADO_VISUALIZAR',
    'FILIADO_INATIVAR',
    'FILIAL_VISUALIZAR_TODAS',
    'RELATORIO_VISUALIZAR_TODOS'
)
WHERE p.codigo = 'MATRIZ_OPERADOR'
ON CONFLICT DO NOTHING;

INSERT INTO perfis_permissoes (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM perfis p
JOIN permissoes pe ON pe.codigo IN ('FILIADO_CRIAR', 'FILIADO_EDITAR', 'FILIADO_VISUALIZAR', 'RELATORIO_VISUALIZAR_FILIAL')
WHERE p.codigo IN ('FILIAL_PROFESSOR', 'FILIAL_RESPONSAVEL')
ON CONFLICT DO NOTHING;

INSERT INTO usuarios (id, nome, email, senha_hash, filial_id, status, data_cadastro)
VALUES (
    '00000000-0000-0000-0000-000000000301',
    'Administrador Matriz',
    'admin@ikonbrasil.com.br',
    '$2a$10$Tm0C8QjV1q52hTtH6dN4M.rhtJ6I/WWDqkD9GxWm9kxbn7HPJAlmC',
    NULL,
    'ATIVO',
    NOW()
)
ON CONFLICT (email) DO NOTHING;

INSERT INTO usuarios_perfis (usuario_id, perfil_id)
VALUES ('00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000101')
ON CONFLICT DO NOTHING;
