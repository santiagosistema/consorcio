package br.com.santiagopinheiro.controller.api;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.santiagopinheiro.dto.ParcelaPagamentoDTO;
import br.com.santiagopinheiro.dto.RegistrarPagamentoDTO;
import br.com.santiagopinheiro.model.Parcela;
import br.com.santiagopinheiro.repository.ParcelaRepository;
import br.com.santiagopinheiro.service.EmailsTodosService;

@RestController
@RequestMapping("/api/pagamento")
public class PagamentoRestController {

    private final ParcelaRepository parcelaRepository;
    private final EmailsTodosService email;

    public PagamentoRestController(
            ParcelaRepository parcelaRepository,
            EmailsTodosService email) {
        this.parcelaRepository = parcelaRepository;
        this.email = email;
    }
    

    @GetMapping("/parcelas")
    @Transactional(readOnly = true)
    public ResponseEntity<?> listarParcelas(
            @RequestParam Long grupoId,
            @RequestParam String mes) {

        try {
            YearMonth anoMes = YearMonth.parse(mes);

            LocalDate inicio = anoMes.atDay(1);
            LocalDate fim = anoMes.atEndOfMonth();

            List<Parcela> parcelas =
                    parcelaRepository
                    .findByGrupo_IdAndDtVencimentoBetweenOrderByUsuario_NomeAscNumeroParcelaAsc(
                            grupoId, inicio, fim);

            LocalDate hoje = LocalDate.now();

            List<ParcelaPagamentoDTO> resultado =
                    parcelas.stream()
                    .map(parcela -> {

                        String situacao;

                        if (parcela.getDtPagamento() != null) {
                            situacao = "paga";
                        } else if (
                                parcela.getDtVencimento()
                                       .isBefore(hoje)) {
                            situacao = "atraso";
                        } else {
                            situacao = "em aberto";
                        }

                        Integer numeroCota =
                                parcela.getCotaParticipante()
                                       .getCota()
                                       .getNumeroCota();

                        return new ParcelaPagamentoDTO(
                                parcela.getId(),
                                parcela.getUsuario().getNome(),
                                numeroCota,
                                parcela.getNumeroParcela(),
                                parcela.getValorDevido(),
                                parcela.getDtVencimento(),
                                parcela.getDtPagamento(),
                                situacao
                        );
                    })
                    .toList();

            return ResponseEntity.ok(resultado);

        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", "Mês inválido. Use o formato AAAA-MM.")
            );
        }
    }

    @PostMapping("/{id}/registrar")
    @Transactional
    public ResponseEntity<?> registrarPagamento(
            @PathVariable Long id,
            @RequestBody RegistrarPagamentoDTO dados) {

        if (dados == null || dados.dataPagamento() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", "Informe a data do pagamento.")
            );
        }

        Parcela parcela = parcelaRepository.findById(id)
                .orElse(null);

        if (parcela == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("erro", "Parcela não encontrada.")
            );
        }

        if (parcela.getDtPagamento() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("erro", "Esta parcela já foi paga.")
            );
        }

        parcela.setDtPagamento(dados.dataPagamento());
        parcela.setSituacao("paga");

        parcelaRepository.save(parcela);
        
        email.emailParcelaPaga(parcela.getUsuario().getEmail(), parcela.getUsuario().getNome(), parcela.getDtVencimento(), parcela.getValorDevido());

        return ResponseEntity.ok(
                Map.of("mensagem", "Pagamento registrado com sucesso.")
        );
    }
}