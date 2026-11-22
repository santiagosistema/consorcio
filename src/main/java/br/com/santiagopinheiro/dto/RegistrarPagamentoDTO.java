package br.com.santiagopinheiro.dto;

import java.time.LocalDate;

public record RegistrarPagamentoDTO(
        LocalDate dataPagamento
) {}