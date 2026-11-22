package br.com.santiagopinheiro.dto;

import java.time.LocalDate;

public record ParcelaPagamentoDTO(
        Long id,
        String usuarioNome,
        Integer numeroCota,
        int numeroParcela,
        double valorDevido,
        LocalDate dtVencimento,
        LocalDate dtPagamento,
        String situacao
) {}