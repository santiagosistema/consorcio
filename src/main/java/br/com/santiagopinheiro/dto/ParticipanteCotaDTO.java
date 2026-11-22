package br.com.santiagopinheiro.dto;

import java.math.BigDecimal;

public record ParticipanteCotaDTO(
    Long usuarioId,
    BigDecimal percentual
) {}