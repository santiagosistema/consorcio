package br.com.santiagopinheiro.controller.web;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.santiagopinheiro.repository.GrupoRepository;
import br.com.santiagopinheiro.service.FinanceiroService;

@Controller
public class PagamentoPageController {

    private final GrupoRepository grupoRepository;
    private final FinanceiroService financeiroService;

    public PagamentoPageController(
            GrupoRepository grupoRepository,
            FinanceiroService financeiroService) {
        this.grupoRepository = grupoRepository;
        this.financeiroService = financeiroService;
    }

    @GetMapping("/pagamento")
    public String pagamento(Model model) {

        model.addAttribute(
                "grupos",
                grupoRepository.findBySituacaoIgnoreCase(
                        "ativo",
                        Sort.by("nomeGrupo").ascending()
                        )
        );

        model.addAttribute(
                "mesAtual",
                YearMonth.now().toString()
        );

        model.addAttribute(
                "dataHoje",
                LocalDate.now().toString()
        );

        return "pagamento";
    }
}