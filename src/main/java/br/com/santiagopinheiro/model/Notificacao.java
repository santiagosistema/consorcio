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
@Table(name = "notificacoes")
public class Notificacao {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@ManyToOne
	@JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "fk_notificacao_usuario"))
	private Usuario usuario;
	
	private String titulo;
	
	@Column(length = 300)
	private String mensagem;
	
	private boolean lida;
	
	@ManyToOne
	@JoinColumn(name = "notificador_id", foreignKey = @ForeignKey(name = "fk_notificacao_notificador"))
	private Usuario userNotificador;
	
	
	@Column(name = "data_criacao")
	private LocalDate dataCriacao;
	
	@Column(name = "url_destino", length = 255)
	private String urlDestino;
	
	@ManyToOne
	@JoinColumn(name = "grupo_id", foreignKey = @ForeignKey(name = "fk_notificacao_grupo"))
	private Grupo grupo;
	
	
	@ManyToOne
	@JoinColumn(name = "parcela_id", foreignKey = @ForeignKey(name = "fk_notificacao_parcela"))
	private Parcela parcela;


	//getter e setter
	public Usuario getUserNotificador() {
		return userNotificador;
	}


	public void setUserNotificador(Usuario userNotificador) {
		this.userNotificador = userNotificador;
	}
	

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	public String getTitulo() {
		return titulo;
	}


	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}


	public String getMensagem() {
		return mensagem;
	}


	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}


	public boolean isLida() {
		return lida;
	}


	public void setLida(boolean lida) {
		this.lida = lida;
	}


	public LocalDate getDataCriacao() {
		return dataCriacao;
	}


	public void setDataCriacao(LocalDate dataCriacao) {
		this.dataCriacao = dataCriacao;
	}


	public String getUrlDestino() {
		return urlDestino;
	}


	public void setUrlDestino(String urlDestino) {
		this.urlDestino = urlDestino;
	}


	public Grupo getGrupo() {
		return grupo;
	}


	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}


	public Parcela getParcela() {
		return parcela;
	}


	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}

	

}
