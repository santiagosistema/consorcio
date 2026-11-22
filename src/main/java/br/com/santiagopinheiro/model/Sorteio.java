package br.com.santiagopinheiro.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Sorteio {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "grupo_id", foreignKey = @ForeignKey(name="fk_sorteio_grupo"))
	private Grupo grupo;
	
	@ManyToOne
	@JoinColumn(name = "cota_id", foreignKey = @ForeignKey(name = "fk_sorteio_cota"))
	private Cota cota;
	
	@Column(name="data_sorteio")
	private LocalDate dataSorteio;
	
	@ManyToOne
	@JoinColumn(name="usuario_sorteado_id", foreignKey = @ForeignKey(name="fk_sorteio_usuario"))
	private Usuario usuarioSorteado;
	
	
	public Grupo getGrupo() {
		return grupo;
	}
	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}
	public Cota getCota() {
		return cota;
	}
	public void setCota(Cota cota) {
		this.cota = cota;
	}
	public LocalDate getDataSorteio() {
		return dataSorteio;
	}
	public void setDataSorteio(LocalDate dataSorteio) {
		this.dataSorteio = dataSorteio;
	}
	public Usuario getUsuarioSorteado() {
		return usuarioSorteado;
	}
	public void setUsuarioSorteado(Usuario usuarioSorteado) {
		this.usuarioSorteado = usuarioSorteado;
	}
	

}
