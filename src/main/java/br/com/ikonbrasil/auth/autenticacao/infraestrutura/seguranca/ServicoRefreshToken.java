package br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca;

import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade.UsuarioJpaEntity;
import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.repositorio.UsuarioJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class ServicoRefreshToken {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final long refreshTokenDias;

    public ServicoRefreshToken(
            RefreshTokenJpaRepository refreshTokenJpaRepository,
            UsuarioJpaRepository usuarioJpaRepository,
            @Value("${ikon.auth.refresh-token-dias}") long refreshTokenDias
    ) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.refreshTokenDias = refreshTokenDias;
    }

    @Transactional
    public String criar(UsuarioJpaEntity usuario, String ipOrigem, String userAgent) {
        String token = UUID.randomUUID() + "." + UUID.randomUUID();
        String hash = hash(token);
        RefreshTokenJpaEntity entidade = new RefreshTokenJpaEntity(
                UUID.randomUUID(),
                usuario,
                hash,
                LocalDateTime.now().plusDays(refreshTokenDias),
                ipOrigem,
                userAgent
        );
        refreshTokenJpaRepository.save(entidade);
        return token;
    }

    @Transactional
    public UsuarioJpaEntity consumirRotacionando(String refreshToken) {
        RefreshTokenJpaEntity entidade = refreshTokenJpaRepository.findByTokenHash(hash(refreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token invalido"));
        if (!entidade.ativoEm(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expirado ou revogado");
        }
        entidade.revogar();
        refreshTokenJpaRepository.save(entidade);
        return usuarioJpaRepository.findById(entidade.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
    }

    @Transactional
    public void revogar(String refreshToken) {
        refreshTokenJpaRepository.findByTokenHash(hash(refreshToken)).ifPresent(entidade -> {
            entidade.revogar();
            refreshTokenJpaRepository.save(entidade);
        });
    }

    private static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponivel", e);
        }
    }
}
