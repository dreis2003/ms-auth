package br.com.ikonbrasil.auth.usuario.infraestrutura.rest.mapper;

import br.com.ikonbrasil.auth.perfil.infraestrutura.banco.entidade.PerfilJpaEntity;
import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade.UsuarioJpaEntity;
import br.com.ikonbrasil.auth.usuario.infraestrutura.rest.dto.saida.UsuarioResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioRestMapper {

    public UsuarioResponse paraResponse(UsuarioJpaEntity usuario) {
        PerfilJpaEntity perfil = usuario.getPerfis().stream()
                .min(Comparator.comparing(PerfilJpaEntity::getCodigo))
                .orElseThrow(() -> new IllegalStateException("Usuario sem perfil"));
        Set<String> permissoes = usuario.getPerfis().stream()
                .flatMap(perfilEntity -> perfilEntity.getPermissoes().stream())
                .map(permissao -> permissao.getCodigo())
                .collect(Collectors.toSet());
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                perfil.getCodigo(),
                usuario.getFilialId(),
                usuario.getStatus(),
                permissoes,
                usuario.getDataCadastro(),
                usuario.getDataAtualizacao()
        );
    }
}
