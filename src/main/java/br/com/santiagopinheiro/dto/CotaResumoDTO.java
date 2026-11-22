package br.com.santiagopinheiro.dto;

import java.util.List;

public record CotaResumoDTO(
		Long id,
		Integer numeroCota,
		List<String> nomes,
		String situacao
		) {

}
