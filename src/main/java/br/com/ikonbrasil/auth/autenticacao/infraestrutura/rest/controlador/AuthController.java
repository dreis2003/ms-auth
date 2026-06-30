package br.com.ikonbrasil.auth.autenticacao.infraestrutura.rest.controlador;

import br.com.ikonbrasil.auth.autenticacao.infraestrutura.rest.dto.entrada.LoginRequest;
import br.com.ikonbrasil.auth.autenticacao.infraestrutura.rest.dto.entrada.RefreshTokenRequest;
import br.com.ikonbrasil.auth.autenticacao.infraestrutura.rest.dto.saida.TokenResponse;
import br.com.ikonbrasil.auth.autenticacao.infraestrutura.rest.dto.saida.UsuarioAutenticadoResponse;
import br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca.ServicoJwt;
import br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca.ServicoRefreshToken;
import br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca.UsuarioAutenticado;
import br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca.CredenciaisInvalidasException;
import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade.UsuarioJpaEntity;
import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.repositorio.UsuarioJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServicoJwt servicoJwt;
    private final ServicoRefreshToken servicoRefreshToken;

    public AuthController(
            UsuarioJpaRepository usuarioJpaRepository,
            PasswordEncoder passwordEncoder,
            ServicoJwt servicoJwt,
            ServicoRefreshToken servicoRefreshToken
    ) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.servicoJwt = servicoJwt;
        this.servicoRefreshToken = servicoRefreshToken;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        UsuarioJpaEntity usuario = usuarioJpaRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(CredenciaisInvalidasException::new);
        if (!"ATIVO".equals(usuario.getStatus()) || !passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        return ResponseEntity.ok(gerarResposta(usuario, servletRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest servletRequest) {
        UsuarioJpaEntity usuario = servicoRefreshToken.consumirRotacionando(request.refreshToken());
        return ResponseEntity.ok(gerarResposta(usuario, servletRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        servicoRefreshToken.revogar(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioAutenticadoResponse> me(Principal principal) {
        UsuarioJpaEntity usuario = usuarioJpaRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
        return ResponseEntity.ok(paraResponse(UsuarioAutenticado.de(usuario)));
    }

    private TokenResponse gerarResposta(UsuarioJpaEntity usuario, HttpServletRequest request) {
        UsuarioAutenticado usuarioAutenticado = UsuarioAutenticado.de(usuario);
        String accessToken = servicoJwt.gerarAccessToken(usuarioAutenticado);
        String refreshToken = servicoRefreshToken.criar(usuario, request.getRemoteAddr(), request.getHeader("User-Agent"));
        return new TokenResponse(
                accessToken,
                refreshToken,
                servicoJwt.expiresInSegundos(),
                "Bearer",
                paraResponse(usuarioAutenticado)
        );
    }

    private static UsuarioAutenticadoResponse paraResponse(UsuarioAutenticado usuario) {
        return new UsuarioAutenticadoResponse(
                usuario.id(),
                usuario.nome(),
                usuario.email(),
                usuario.perfil(),
                usuario.filialId(),
                usuario.permissoes()
        );
    }
}
