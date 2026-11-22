package br.com.santiagopinheiro.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(
    name = "cotas",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cota_grupo_numero",
            columnNames = {"grupo_id", "numero_cota"}
        )
    }
)
public class Cota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cota_grupo"))
    private Grupo grupo;

    @Column(name = "numero_cota", nullable = false)
    private Integer numeroCota;

    @Column(name = "situacao_cota")
    private String situacaoCota;

    @Column(name = "data_aquisicao")
    private LocalDate dataAquisicao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Integer getNumeroCota() {
        return numeroCota;
    }

    public void setNumeroCota(Integer numeroCota) {
        this.numeroCota = numeroCota;
    }

    public String getSituacaoCota() {
        return situacaoCota;
    }

    public void setSituacaoCota(String situacaoCota) {
        this.situacaoCota = situacaoCota;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(LocalDate dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }
}