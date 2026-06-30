package br.com.ikonbrasil.auth.perfil.infraestrutura.banco.repositorio;

import br.com.ikonbrasil.auth.perfil.infraestrutura.banco.entidade.PerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PerfilJpaRepository extends JpaRepository<PerfilJpaEntity, UUID> {

    Optional<PerfilJpaEntity> findByCodigo(String codigo);
}
