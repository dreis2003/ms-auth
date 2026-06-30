package br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade;

import br.com.ikonbrasil.auth.perfil.infraestrutura.banco.entidade.PerfilJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "filial_id")
    private UUID filialId;

    @Column(length = 30)
    private String telefone;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_perfis",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "perfil_id")
    )
    private Set<PerfilJpaEntity> perfis = new HashSet<>();

    protected UsuarioJpaEntity() {
    }

    public static UsuarioJpaEntity criar(
            String nome,
            String email,
            String senhaHash,
            String telefone,
            UUID filialId,
            PerfilJpaEntity perfil
    ) {
        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.id = UUID.randomUUID();
        usuario.nome = nome;
        usuario.email = email;
        usuario.senhaHash = senhaHash;
        usuario.telefone = telefone;
        usuario.filialId = filialId;
        usuario.status = "ATIVO";
        usuario.dataCadastro = LocalDateTime.now();
        usuario.perfis.add(perfil);
        return usuario;
    }

    public void ativar() {
        this.status = "ATIVO";
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void inativar() {
        this.status = "INATIVO";
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void atualizarDados(String nome, String email, String telefone, UUID filialId, PerfilJpaEntity perfil) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.filialId = filialId;
        this.perfis.clear();
        this.perfis.add(perfil);
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void alterarSenha(String senhaHash) {
        this.senhaHash = senhaHash;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public UUID getFilialId() {
        return filialId;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public Set<PerfilJpaEntity> getPerfis() {
        return perfis;
    }
}
