package br.com.ikonbrasil.auth.autenticacao.infraestrutura.rest.dto.saida;

import java.util.Set;
import java.util.UUID;

public record UsuarioAutenticadoResponse(
        UUID id,
        String nome,
        String email,
        String perfil,
        UUID filialId,
        Set<String> permissoes
) {
}
