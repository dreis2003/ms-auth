package br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);
}
