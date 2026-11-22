package br.com.santiagopinheiro.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.santiagopinheiro.repository.ParcelaRepository;

@Service
public class FinanceiroService {

    private final ParcelaRepository parcelaRepository;

    public FinanceiroService(ParcelaRepository parcelaRepository) {
        this.parcelaRepository = parcelaRepository;
    }

    @Transactional
    public int atualizarParcelasVencidas() {
        return parcelaRepository.atualizarParcelasVencidas(
            LocalDate.now()
        );
    }
}