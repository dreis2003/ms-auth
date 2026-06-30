package br.com.ikonbrasil.auth.usuario.infraestrutura.servico;

import br.com.ikonbrasil.auth.perfil.infraestrutura.banco.entidade.PerfilJpaEntity;
import br.com.ikonbrasil.auth.perfil.infraestrutura.banco.repositorio.PerfilJpaRepository;
import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade.UsuarioJpaEntity;
import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.repositorio.UsuarioJpaRepository;
import br.com.ikonbrasil.auth.usuario.infraestrutura.rest.dto.entrada.AtualizarUsuarioRequest;
import br.com.ikonbrasil.auth.usuario.infraestrutura.rest.dto.entrada.CriarUsuarioRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final PerfilJpaRepository perfilJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioJpaRepository usuarioJpaRepository,
            PerfilJpaRepository perfilJpaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.perfilJpaRepository = perfilJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioJpaEntity criar(CriarUsuarioRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarioJpaRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ja existe usuario cadastrado com este email");
        }

        String codigoPerfil = request.perfil().trim().toUpperCase();
        PerfilJpaEntity perfil = perfilJpaRepository.findByCodigo(codigoPerfil)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de acesso nao encontrado"));
        validarVinculoFilial(codigoPerfil, request.filialId());

        UsuarioJpaEntity usuario = UsuarioJpaEntity.criar(
                request.nome().trim(),
                email,
                passwordEncoder.encode(request.senha()),
                normalizarTextoOpcional(request.telefone()),
                request.filialId(),
                perfil
        );
        return usuarioJpaRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioJpaEntity buscarPorId(UUID id) {
        return usuarioJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
    }

    @Transactional
    public UsuarioJpaEntity atualizar(UUID id, AtualizarUsuarioRequest request) {
        UsuarioJpaEntity usuario = buscarPorId(id);
        String email = request.email().trim().toLowerCase();
        if (usuarioJpaRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("Ja existe usuario cadastrado com este email");
        }

        String codigoPerfil = request.perfil().trim().toUpperCase();
        PerfilJpaEntity perfil = perfilJpaRepository.findByCodigo(codigoPerfil)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de acesso nao encontrado"));
        validarVinculoFilial(codigoPerfil, request.filialId());

        usuario.atualizarDados(request.nome().trim(), email, normalizarTextoOpcional(request.telefone()), request.filialId(), perfil);
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.alterarSenha(passwordEncoder.encode(request.senha()));
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public List<UsuarioJpaEntity> listar() {
        return usuarioJpaRepository.findAll();
    }

    @Transactional
    public UsuarioJpaEntity ativar(UUID id) {
        UsuarioJpaEntity usuario = buscarPorId(id);
        usuario.ativar();
        return usuario;
    }

    @Transactional
    public UsuarioJpaEntity inativar(UUID id) {
        UsuarioJpaEntity usuario = buscarPorId(id);
        usuario.inativar();
        return usuario;
    }

    private static void validarVinculoFilial(String codigoPerfil, UUID filialId) {
        if (codigoPerfil.startsWith("FILIAL_") && filialId == null) {
            throw new IllegalArgumentException("Usuario de filial deve possuir filialId");
        }
        if (codigoPerfil.startsWith("MATRIZ_") && filialId != null) {
            throw new IllegalArgumentException("Usuario da matriz nao deve possuir filialId");
        }
    }

    private static String normalizarTextoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
