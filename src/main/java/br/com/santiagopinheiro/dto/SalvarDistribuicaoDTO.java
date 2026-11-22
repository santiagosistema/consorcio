package br.com.santiagopinheiro.dto;

import java.util.List;

public record SalvarDistribuicaoDTO(
    Long grupoId,
    List<CotaDistribuicaoDTO> cotas
) {}