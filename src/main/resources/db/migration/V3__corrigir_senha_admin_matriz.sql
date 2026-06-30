UPDATE usuarios
SET senha_hash = '$2a$10$MskdGUVlTAfRZ41skSp8zuhsZHZ5de6YmgJz1T4wkhIzonejnnzGm',
    data_atualizacao = NOW()
WHERE email = 'admin@ikonbrasil.com.br';
