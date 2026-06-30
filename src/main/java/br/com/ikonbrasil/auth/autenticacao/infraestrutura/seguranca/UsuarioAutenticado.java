package br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca;

import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade.UsuarioJpaEntity;

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UsuarioAutenticado(
        UUID id,
        String nome,
        String email,
        String perfil,
        UUID filialId,
        Set<String> permissoes
) {

    public static UsuarioAutenticado de(UsuarioJpaEntity usuario) {
        String perfil = usuario.getPerfis().stream()
                .map(perfilEntity -> perfilEntity.getCodigo())
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalStateException("Usuario sem perfil"));

        Set<String> permissoes = usuario.getPerfis().stream()
                .flatMap(perfilEntity -> perfilEntity.getPermissoes().stream())
                .map(permissao -> permissao.getCodigo())
                .collect(Collectors.toSet());

        return new UsuarioAutenticado(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                perfil,
                usuario.getFilialId(),
                permissoes
        );
    }
}
