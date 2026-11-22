package br.com.santiagopinheiro.dto;

import java.util.List;

public record CotaDistribuicaoDTO(
    Integer numeroCota,
    List<ParticipanteCotaDTO> participantes
) {}