package br.com.ikonbrasil.auth.compartilhado.infraestrutura.configuracao;

import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade.UsuarioJpaEntity;
import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.repositorio.UsuarioJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class ConfiguracaoSeguranca {

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/api/v1/auth/login",
                "/api/v1/auth/refresh",
                "/actuator/health",
                "/actuator/info",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**"
        );
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/actuator/health",
                                "/actuator/info",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    UserDetailsService userDetailsService(UsuarioJpaRepository usuarioJpaRepository) {
        return email -> {
            UsuarioJpaEntity usuario = usuarioJpaRepository.findByEmail(email.toLowerCase())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
            String[] roles = usuario.getPerfis().stream().map(perfil -> perfil.getCodigo()).toArray(String[]::new);
            return User.withUsername(usuario.getEmail())
                    .password(usuario.getSenhaHash())
                    .disabled(!"ATIVO".equals(usuario.getStatus()))
                    .roles(roles)
                    .build();
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${ikon.auth.jwt-secret}") String jwtSecret) {
        SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("email");
        converter.setJwtGrantedAuthoritiesConverter(this::extrairAuthorities);
        return converter;
    }

    private Collection<GrantedAuthority> extrairAuthorities(Jwt jwt) {
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        String perfil = jwt.getClaimAsString("perfil");
        if (perfil != null && !perfil.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + perfil));
        }
        List<String> permissoes = jwt.getClaimAsStringList("permissoes");
        if (permissoes != null) {
            permissoes.stream()
                    .filter(permissao -> permissao != null && !permissao.isBlank())
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
        return authorities;
    }
}
