package br.com.santiagopinheiro.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(
    name = "cota_participantes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cota_participante_usuario",
            columnNames = {"cota_id", "usuario_id"}
        )
    }
)
public class CotaParticipante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cota_id", nullable = false)
    private Cota cota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "grupo_id", foreignKey = @ForeignKey(name = "cotaParticipante_grupo"))
    private Grupo grupo;
    

    @Column(
        name = "percentual_participacao",
        nullable = false,
        precision = 5,
        scale = 2
    )
    private BigDecimal percentualParticipacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cota getCota() {
        return cota;
    }

    public void setCota(Cota cota) {
        this.cota = cota;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getPercentualParticipacao() {
        return percentualParticipacao;
    }

    public void setPercentualParticipacao(
            BigDecimal percentualParticipacao) {
        this.percentualParticipacao = percentualParticipacao;
    }

	public Grupo getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}
    
    
}