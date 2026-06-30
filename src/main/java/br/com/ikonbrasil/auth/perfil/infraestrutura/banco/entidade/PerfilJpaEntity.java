package br.com.ikonbrasil.auth.perfil.infraestrutura.banco.entidade;

import br.com.ikonbrasil.auth.permissao.infraestrutura.banco.entidade.PermissaoJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "perfis")
public class PerfilJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "perfis_permissoes",
            joinColumns = @JoinColumn(name = "perfil_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private Set<PermissaoJpaEntity> permissoes = new HashSet<>();

    protected PerfilJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Set<PermissaoJpaEntity> getPermissoes() {
        return permissoes;
    }
}
