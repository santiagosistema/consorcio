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
import jakarta.persistence.Table;

@Entity
@Table(name = "parcelas")
public class Parcela {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "grupo_id", foreignKey = @ForeignKey(name = "fk_parcela_grupo"))
	private Grupo grupo;
	
	@ManyToOne
	@JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "fk_parcela_usuario"))
	private Usuario usuario;
	
	@Column(name="numero_parcela")
	private int numeroParcela;
	
	@Column(name = "valor_devido")
	private double valorDevido;
	
	@Column(name = "dt_vencimento")
	private LocalDate dtVencimento;
	
	@Column(name = "dt_pagamento")
	private LocalDate dtPagamento;
	
	//pago, atraso, futura
	private String situacao;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "cota_participante_id", nullable = false)
	private CotaParticipante cotaParticipante;

	
	
	
	public CotaParticipante getCotaParticipante() {
	    return cotaParticipante;
	}

	public void setCotaParticipante(
	        CotaParticipante cotaParticipante) {
	    this.cotaParticipante = cotaParticipante;
	}
	
	
	


	public Grupo getGrupo() {
		return grupo;
	}


	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public int getNumeroParcela() {
		return numeroParcela;
	}


	public void setNumeroParcela(int numeroParcela) {
		this.numeroParcela = numeroParcela;
	}


	public double getValorDevido() {
		return valorDevido;
	}


	public void setValorDevido(double valorDevido) {
		this.valorDevido = valorDevido;
	}


	public LocalDate getDtVencimento() {
		return dtVencimento;
	}


	public void setDtVencimento(LocalDate dtVencimento) {
		this.dtVencimento = dtVencimento;
	}


	public LocalDate getDtPagamento() {
		return dtPagamento;
	}


	public void setDtPagamento(LocalDate dtPagamento) {
		this.dtPagamento = dtPagamento;
	}


	public String getSituacao() {
		return situacao;
	}


	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	
	
	

}
