package br.com.ikonbrasil.auth.autenticacao.infraestrutura.rest.dto.entrada;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String senha
) {
}
