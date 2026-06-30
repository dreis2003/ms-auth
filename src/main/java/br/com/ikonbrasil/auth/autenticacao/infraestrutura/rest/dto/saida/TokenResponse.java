package br.com.ikonbrasil.auth.autenticacao.infraestrutura.rest.dto.saida;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        UsuarioAutenticadoResponse usuario
) {
}
