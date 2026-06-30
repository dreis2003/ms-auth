package br.com.ikonbrasil.auth.permissao.infraestrutura.banco.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "permissoes")
public class PermissaoJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String codigo;

    @Column(length = 255)
    private String descricao;

    protected PermissaoJpaEntity() {
    }

    public String getCodigo() {
        return codigo;
    }
}
