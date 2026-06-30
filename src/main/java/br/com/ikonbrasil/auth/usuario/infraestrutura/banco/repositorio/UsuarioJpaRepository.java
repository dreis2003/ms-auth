package br.com.ikonbrasil.auth.usuario.infraestrutura.banco.repositorio;

import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

    Optional<UsuarioJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
