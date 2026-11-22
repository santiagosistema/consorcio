package br.com.santiagopinheiro.controller.web;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.santiagopinheiro.model.Parcela;
import br.com.santiagopinheiro.model.Usuario;
import br.com.santiagopinheiro.repository.GrupoRepository;
import br.com.santiagopinheiro.repository.ParcelaRepository;
import br.com.santiagopinheiro.service.FinanceiroService;


@Controller
@RequestMapping("/parcelas")
public class ParcelaController {

    private final GrupoRepository grupoRepository;
    private final ParcelaRepository parcelaRepository;
    private final FinanceiroService financeiroService;

    public ParcelaController(
            GrupoRepository grupoRepository,
            ParcelaRepository parcelaRepository,
            FinanceiroService financeiroService) {
        this.grupoRepository = grupoRepository;
        this.parcelaRepository = parcelaRepository;
        this.financeiroService = financeiroService;
        
    }

    @GetMapping
    public String consultar(
            @RequestParam(required = false) Long grupoId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String mes,
            @RequestParam(required = false) Long usuarioId,
            Model model) {
    	
    	financeiroService.atualizarParcelasVencidas();

        // Sempre carregar os grupos para o primeiro select
        model.addAttribute(
                "grupos", grupoRepository.findBySituacaoIgnoreCase(
                        "ativo",
                        Sort.by("nomeGrupo").ascending()
                        )
                );

        model.addAttribute("grupoId", grupoId);
        model.addAttribute("tipo", tipo);
        model.addAttribute("mes", mes);
        model.addAttribute("usuarioId", usuarioId);

        // Sem grupo, apenas exibe a tela inicial
        if (grupoId == null) {
            return "parcelas";
        }

        // Carregar os usuários disponíveis para o grupo
        List<Usuario> usuarios =
                parcelaRepository.buscarUsuariosDoGrupo(grupoId);
        
        for (Usuario usuario : usuarios) {
			String nome = usuario.getNome().trim();
			//ignoreCase: (?i)  pega qualquer letra com e sem acento: \p{L}
			nome = nome.replaceAll(
					"(?i)^(\\p{L}+)\\s+(da\\s+\\p{L}+|de\\s+\\p{L}+|do\\s+\\p{L}+|das\\s+\\p{L}+|dos\\s+\\p{L}+|\\p{L}+).*","$1 $2"
					);
			
			usuario.setNome(nome);
		}

        model.addAttribute("usuarios", usuarios);

        // Nenhuma consulta solicitada ainda
        if (tipo == null || tipo.isBlank()) {
            return "parcelas";
        }

        List<Parcela> parcelas = List.of();

        // CONSULTA POR MÊS
        if ("mes".equals(tipo)
                && mes != null
                && !mes.isBlank()) {

            YearMonth anoMes = YearMonth.parse(mes);

            LocalDate inicio = anoMes.atDay(1);
            LocalDate fim = anoMes.atEndOfMonth();

            parcelas = parcelaRepository
                    .findByGrupo_IdAndDtVencimentoBetweenOrderByUsuario_NomeAscNumeroParcelaAsc(
                            grupoId, inicio, fim);
        }

        // CONSULTA POR USUÁRIO
        else if ("usuario".equals(tipo)
                && usuarioId != null) {

            parcelas = parcelaRepository
                    .findByGrupo_IdAndUsuario_IdOrderByNumeroParcelaAsc(
                            grupoId, usuarioId);
        }

        model.addAttribute("parcelas", parcelas);

        return "parcelas";
    }
}