INSERT INTO perfis_permissoes (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM perfis p
JOIN permissoes pe ON pe.codigo = 'FILIADO_INATIVAR'
WHERE p.codigo IN ('FILIAL_PROFESSOR', 'FILIAL_RESPONSAVEL')
ON CONFLICT DO NOTHING;
