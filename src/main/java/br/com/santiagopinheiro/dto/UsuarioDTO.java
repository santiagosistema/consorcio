package br.com.santiagopinheiro.dto;

	
	public record UsuarioDTO(
		    Long id,
		    String nome,
		    String cpf,
		    String email,
		    boolean situacao,
		    boolean administrador
		) {}
