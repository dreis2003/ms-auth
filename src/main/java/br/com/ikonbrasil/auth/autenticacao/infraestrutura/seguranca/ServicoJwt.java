package br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class ServicoJwt {

    private final SecretKey chave;
    private final long accessTokenMinutos;

    public ServicoJwt(
            @Value("${ikon.auth.jwt-secret}") String segredo,
            @Value("${ikon.auth.access-token-minutos}") long accessTokenMinutos
    ) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMinutos = accessTokenMinutos;
    }

    public String gerarAccessToken(UsuarioAutenticado usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(accessTokenMinutos * 60);

        return Jwts.builder()
                .issuer("ms-auth")
                .subject(usuario.id().toString())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .claims(Map.of(
                        "userId", usuario.id().toString(),
                        "nome", usuario.nome(),
                        "email", usuario.email(),
                        "perfil", usuario.perfil(),
                        "filialId", usuario.filialId() == null ? "" : usuario.filialId().toString(),
                        "permissoes", usuario.permissoes()
                ))
                .signWith(chave, Jwts.SIG.HS256)
                .compact();
    }

    public long expiresInSegundos() {
        return accessTokenMinutos * 60;
    }
}
