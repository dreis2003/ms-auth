package br.com.ikonbrasil.auth.usuario.infraestrutura.rest.dto.entrada;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarUsuarioRequest(
        @NotBlank
        @Size(max = 150)
        String nome,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @Size(max = 30)
        String telefone,

        @NotBlank
        @Size(min = 8, max = 72)
        String senha,

        @NotBlank
        String perfil,

        UUID filialId
) {
}
