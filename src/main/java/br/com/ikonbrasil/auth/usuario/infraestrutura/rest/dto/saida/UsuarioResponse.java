package br.com.ikonbrasil.auth.usuario.infraestrutura.rest.dto.saida;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        String telefone,
        String perfil,
        UUID filialId,
        String status,
        Set<String> permissoes,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao
) {
}
