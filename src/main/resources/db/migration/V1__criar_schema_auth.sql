CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    filial_id UUID,
    status VARCHAR(20) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT ck_usuarios_status CHECK (status IN ('ATIVO', 'INATIVO'))
);

CREATE TABLE perfis (
    id UUID PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    CONSTRAINT uk_perfis_codigo UNIQUE (codigo),
    CONSTRAINT ck_perfis_codigo CHECK (codigo IN ('MATRIZ_ADMIN', 'MATRIZ_OPERADOR', 'FILIAL_PROFESSOR', 'FILIAL_RESPONSAVEL'))
);

CREATE TABLE permissoes (
    id UUID PRIMARY KEY,
    codigo VARCHAR(80) NOT NULL,
    descricao VARCHAR(255),
    CONSTRAINT uk_permissoes_codigo UNIQUE (codigo)
);

CREATE TABLE usuarios_perfis (
    usuario_id UUID NOT NULL,
    perfil_id UUID NOT NULL,
    PRIMARY KEY (usuario_id, perfil_id),
    CONSTRAINT fk_usuarios_perfis_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_usuarios_perfis_perfil FOREIGN KEY (perfil_id) REFERENCES perfis(id)
);

CREATE TABLE perfis_permissoes (
    perfil_id UUID NOT NULL,
    permissao_id UUID NOT NULL,
    PRIMARY KEY (perfil_id, permissao_id),
    CONSTRAINT fk_perfis_permissoes_perfil FOREIGN KEY (perfil_id) REFERENCES perfis(id),
    CONSTRAINT fk_perfis_permissoes_permissao FOREIGN KEY (permissao_id) REFERENCES permissoes(id)
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expira_em TIMESTAMP NOT NULL,
    revogado BOOLEAN NOT NULL,
    revogado_em TIMESTAMP,
    criado_em TIMESTAMP NOT NULL,
    ip_origem VARCHAR(80),
    user_agent TEXT,
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_usuarios_filial_id ON usuarios(filial_id);
CREATE INDEX idx_refresh_tokens_usuario_id ON refresh_tokens(usuario_id);
CREATE INDEX idx_refresh_tokens_revogado ON refresh_tokens(revogado);
