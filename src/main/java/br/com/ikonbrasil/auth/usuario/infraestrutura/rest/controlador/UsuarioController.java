package br.com.ikonbrasil.auth.usuario.infraestrutura.rest.controlador;

import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade.UsuarioJpaEntity;
import br.com.ikonbrasil.auth.usuario.infraestrutura.rest.dto.entrada.AtualizarUsuarioRequest;
import br.com.ikonbrasil.auth.usuario.infraestrutura.rest.dto.entrada.CriarUsuarioRequest;
import br.com.ikonbrasil.auth.usuario.infraestrutura.rest.dto.saida.UsuarioResponse;
import br.com.ikonbrasil.auth.usuario.infraestrutura.rest.mapper.UsuarioRestMapper;
import br.com.ikonbrasil.auth.usuario.infraestrutura.servico.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Gestao de usuarios do sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRestMapper usuarioRestMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioRestMapper usuarioRestMapper) {
        this.usuarioService = usuarioService;
        this.usuarioRestMapper = usuarioRestMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('MATRIZ_ADMIN')")
    @Operation(summary = "Criar usuario")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody CriarUsuarioRequest request) {
        UsuarioJpaEntity usuario = usuarioService.criar(request);
        return ResponseEntity
                .created(URI.create("/api/v1/usuarios/" + usuario.getId()))
                .body(usuarioRestMapper.paraResponse(usuario));
    }

    @GetMapping
    @PreAuthorize("hasRole('MATRIZ_ADMIN')")
    @Operation(summary = "Listar usuarios")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar().stream()
                .map(usuarioRestMapper::paraResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MATRIZ_ADMIN')")
    @Operation(summary = "Buscar usuario por ID")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioRestMapper.paraResponse(usuarioService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MATRIZ_ADMIN')")
    @Operation(summary = "Atualizar usuario")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarUsuarioRequest request
    ) {
        return ResponseEntity.ok(usuarioRestMapper.paraResponse(usuarioService.atualizar(id, request)));
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('MATRIZ_ADMIN')")
    @Operation(summary = "Ativar usuario")
    public ResponseEntity<UsuarioResponse> ativar(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioRestMapper.paraResponse(usuarioService.ativar(id)));
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('MATRIZ_ADMIN')")
    @Operation(summary = "Inativar usuario")
    public ResponseEntity<UsuarioResponse> inativar(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioRestMapper.paraResponse(usuarioService.inativar(id)));
    }
}
