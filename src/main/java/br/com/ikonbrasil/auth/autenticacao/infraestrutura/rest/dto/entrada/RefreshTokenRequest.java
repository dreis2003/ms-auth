package br.com.ikonbrasil.auth.autenticacao.infraestrutura.rest.dto.entrada;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank String refreshToken) {
}
