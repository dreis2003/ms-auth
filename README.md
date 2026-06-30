# ms-auth

Microsservico de autenticacao e autorizacao da plataforma IKO Nakamura Brasil.

## Responsabilidades

- Autenticar usuarios do sistema.
- Gerenciar perfis e permissoes.
- Emitir access token JWT.
- Emitir, armazenar e revogar refresh tokens.
- Armazenar senhas com BCrypt.

## Banco De Dados

Profile `dev`:

- Banco: `db_ikon_auth_dev`
- Host: `192.168.40.80`
- Usuario: `ikon_dev`

## Usuario Inicial

Usuario criado via migration para desenvolvimento:

```text
email: admin@ikonbrasil.com.br
senha: admin123
perfil: MATRIZ_ADMIN
```

## Como Rodar

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Porta padrao:

```text
http://localhost:8081
```

Swagger:

```text
http://localhost:8081/swagger-ui.html
```

## Endpoints

```http
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
GET  /api/v1/auth/me

POST  /api/v1/usuarios
GET   /api/v1/usuarios
GET   /api/v1/usuarios/{id}
PATCH /api/v1/usuarios/{id}/ativar
PATCH /api/v1/usuarios/{id}/inativar
```

## Login

```json
{
  "email": "admin@ikonbrasil.com.br",
  "senha": "admin123"
}
```

Resposta:

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "expiresIn": 900,
  "tokenType": "Bearer",
  "usuario": {
    "id": "...",
    "nome": "Administrador Matriz",
    "email": "admin@ikonbrasil.com.br",
    "perfil": "MATRIZ_ADMIN",
    "filialId": null,
    "permissoes": []
  }
}
```

## Observacoes

- JWT usa HMAC nesta primeira entrega.
- A proxima evolucao recomendada e migrar para RS256 e publicar JWKS para os demais microsservicos validarem usando chave publica.
- Refresh token e persistido como hash SHA-256.

## Gestao De Usuarios

Os endpoints de usuarios exigem token Bearer de um usuario com perfil `MATRIZ_ADMIN`.

Criar usuario da matriz:

```json
{
  "nome": "Operador Matriz",
  "email": "operador@ikonbrasil.com.br",
  "senha": "operador123",
  "perfil": "MATRIZ_OPERADOR",
  "filialId": null
}
```

Criar usuario de filial:

```json
{
  "nome": "Responsavel Dojo Centro",
  "email": "responsavel.centro@ikonbrasil.com.br",
  "senha": "responsavel123",
  "perfil": "FILIAL_RESPONSAVEL",
  "filialId": "UUID_DA_FILIAL"
}
```

Regras protegidas:

- Email de usuario deve ser unico.
- Senha e armazenada com BCrypt.
- Perfis de filial exigem `filialId`.
- Perfis de matriz nao aceitam `filialId`.
- Apenas `MATRIZ_ADMIN` cria, lista, ativa e inativa usuarios.
