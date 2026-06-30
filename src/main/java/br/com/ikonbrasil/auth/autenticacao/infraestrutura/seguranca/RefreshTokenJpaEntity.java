package br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca;

import br.com.ikonbrasil.auth.usuario.infraestrutura.banco.entidade.UsuarioJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(nullable = false)
    private boolean revogado;

    @Column(name = "revogado_em")
    private LocalDateTime revogadoEm;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "ip_origem", length = 80)
    private String ipOrigem;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    protected RefreshTokenJpaEntity() {
    }

    public RefreshTokenJpaEntity(UUID id, UsuarioJpaEntity usuario, String tokenHash, LocalDateTime expiraEm, String ipOrigem, String userAgent) {
        this.id = id;
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.expiraEm = expiraEm;
        this.revogado = false;
        this.criadoEm = LocalDateTime.now();
        this.ipOrigem = ipOrigem;
        this.userAgent = userAgent;
    }

    public boolean ativoEm(LocalDateTime agora) {
        return !revogado && expiraEm.isAfter(agora);
    }

    public void revogar() {
        this.revogado = true;
        this.revogadoEm = LocalDateTime.now();
    }

    public UsuarioJpaEntity getUsuario() {
        return usuario;
    }
}
