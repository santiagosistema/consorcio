package br.com.santiagopinheiro.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "grupos")
public class Grupo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome_grupo")
	private String nomeGrupo;
	
	@Column(name = "valor_cota")
	private double valorCota;
	
	@Column(name = "total_cotas")
	private int totalCotas;
	
	@Column(name = "data_inicio")
	private LocalDate dataInicio;
	
	@Column(name = "data_fim")
	private LocalDate dataFim;

	//ativo, encerrado, cancelado
	private String situacao;
	
	@Column(name = "data_situacao")
	private LocalDate dataSituacao;
	
	
	
	public int getTotalCotas() {
		return totalCotas;
	}
	
	public void setTotalCotas(int totalCotas) {
		this.totalCotas = totalCotas;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeGrupo() {
		return nomeGrupo;
	}

	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}

	public double getValorCota() {
		return valorCota;
	}

	public void setValorCota(double valorCotaBase) {
		this.valorCota = valorCotaBase;
	}

	public LocalDate getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(LocalDate dataInicio) {
		this.dataInicio = dataInicio;
	}

	public LocalDate getDataFim() {
		return dataFim;
	}

	public void setDataFim(LocalDate dataFim) {
		this.dataFim = dataFim;
	}

	public LocalDate getDataSituacao() {
		return dataSituacao;
	}

	public void setDataSituacao(LocalDate dataSituacao) {
		this.dataSituacao = dataSituacao;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	
	
	

}
