package br.com.santiagopinheiro.controller.web;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import br.com.santiagopinheiro.dto.CotaResumoDTO;
import br.com.santiagopinheiro.dto.ParticipanteVisaoGeralDTO;
import br.com.santiagopinheiro.model.Cota;
import br.com.santiagopinheiro.model.CotaParticipante;
import br.com.santiagopinheiro.model.Grupo;
import br.com.santiagopinheiro.model.Parcela;
import br.com.santiagopinheiro.repository.CotaParticipanteRepository;
import br.com.santiagopinheiro.repository.CotaRepository;
import br.com.santiagopinheiro.repository.GrupoRepository;
import br.com.santiagopinheiro.repository.VisaoGeralRepository;

@Controller
@RequestMapping("/grupo")
public class VisaoGeralController {

    private final GrupoRepository grupoRepository;
    private final CotaRepository cotaRepository;
    private final CotaParticipanteRepository cotaParticipanteRepository;
    private final VisaoGeralRepository visaoGeralRepository;

    public VisaoGeralController(
            GrupoRepository grupoRepository,
            CotaRepository cotaRepository,
            CotaParticipanteRepository cotaParticipanteRepository,
            VisaoGeralRepository visaoGeralRepository) {
        this.grupoRepository = grupoRepository;
        this.cotaRepository = cotaRepository;
        this.cotaParticipanteRepository = cotaParticipanteRepository;
        this.visaoGeralRepository = visaoGeralRepository;
    }

    @GetMapping("/visao-geral")
    public String abrirVisaoGeral(
            @RequestParam(required = false) Long grupoId,
            Model model) {

        List<Grupo> gruposAtivos = grupoRepository.findBySituacaoIgnoreCase(
                "ativo",
                Sort.by("nomeGrupo").ascending());

        model.addAttribute("gruposAtivos", gruposAtivos);
        model.addAttribute("grupoSelecionadoId", grupoId);

        // Sem grupo selecionado, a página exibe apenas o seletor.
        if (grupoId == null) {
            return "visao-geral";
        }

        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Grupo não encontrado"));

        if (!"ativo".equalsIgnoreCase(grupo.getSituacao())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O grupo selecionado não está ativo");
        }

        model.addAttribute("grupo", grupo);

        //pega todas as cotas do grupo
        List<Cota> cotas = cotaRepository.findByGrupo_IdOrderByNumeroCotaAsc(grupoId);
        
        List<CotaResumoDTO> cotasDto = new ArrayList<>();
        
        for(Cota cota : cotas) {
        	List<CotaParticipante> cotasParticipantes = cotaParticipanteRepository.findByCota_IdOrderByUsuario_NomeAsc(cota.getId());
        	
        	List<String> nomes = new ArrayList<>();
        	
        	for(CotaParticipante c : cotasParticipantes) {
        		String name = c.getUsuario().getNome();
        		name = primeiroNome(name);
        		nomes.add(name);
        	}
        	
        	CotaResumoDTO cotadto = new CotaResumoDTO(
        			cota.getId(),
        			cota.getNumeroCota(),
        			nomes,
        			cota.getSituacaoCota()
        			);
        	
        	cotasDto.add(cotadto);
        }
        
        Set<Long> idsCotasContempladas = new LinkedHashSet<>();

        for (Cota cota : cotas) {
            if (ehContemplada(cota.getSituacaoCota())) {
                idsCotasContempladas.add(cota.getId());
            }
        }

        List<CotaParticipante> vinculos =
                cotaParticipanteRepository.findByCota_Grupo_Id(grupoId);

        Map<Long, ResumoParticipante> participantes = new LinkedHashMap<>();

        for (CotaParticipante vinculo : vinculos) {
            if (vinculo.getUsuario() == null || vinculo.getCota() == null) {
                continue;
            }

            Long usuarioId = vinculo.getUsuario().getId();
            ResumoParticipante resumo = participantes.computeIfAbsent(
                    usuarioId,
                    id -> new ResumoParticipante(
                            primeiroNome(vinculo.getUsuario().getNome())));

            Long cotaId = vinculo.getCota().getId();
            resumo.idsCotas.add(cotaId);

            if (idsCotasContempladas.contains(cotaId)) {
                resumo.temCotaContemplada = true;
            }
        }

        List<ParticipanteVisaoGeralDTO> listaParticipantes = new ArrayList<>();

        for (ResumoParticipante resumo : participantes.values()) {
            listaParticipantes.add(new ParticipanteVisaoGeralDTO(
                    resumo.primeiroNome,
                    resumo.idsCotas.size(),
                    resumo.temCotaContemplada));
        }

        listaParticipantes.sort(Comparator.comparing(
                ParticipanteVisaoGeralDTO::getPrimeiroNome,
                String.CASE_INSENSITIVE_ORDER));

        long totalCotasGrupo = Math.max(grupo.getTotalCotas(), 0);
        long totalCotasCadastradas = cotas.size();
        long totalContempladas = idsCotasContempladas.size();
        long totalNaoContempladas = Math.max(
                totalCotasCadastradas - totalContempladas, 0);
        String dataFim = grupo.getDataFim().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        
        //dia 15 eu troco o mês (tirei isso por enquanto)
        LocalDate dataAtual = LocalDate.now();
        List<Parcela> parcelasDoMes;
        String nomeMesReferencia = "";
        
        LocalDate primeiroDia = dataAtual.withDayOfMonth(1);
        LocalDate ultimoDia = dataAtual.withDayOfMonth(dataAtual.lengthOfMonth());
        parcelasDoMes = visaoGeralRepository.listParcelasDoMes(primeiroDia, ultimoDia);
        nomeMesReferencia = primeiroDia.format(DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("pt-BR")));
        		
        /*	
        if(dataAtual.getDayOfMonth() < 15) {
        	LocalDate primeiroDia = dataAtual.withDayOfMonth(1);
            LocalDate ultimoDia = dataAtual.withDayOfMonth(dataAtual.lengthOfMonth());
            parcelasDoMes = visaoGeralRepository.listParcelasDoMes(primeiroDia, ultimoDia);
            nomeMesReferencia = primeiroDia.format(DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("pt-BR")));
        }else {
        	dataAtual = dataAtual.plusMonths(1);
        	LocalDate primeiroDia = dataAtual.withDayOfMonth(1);
            LocalDate ultimoDia = dataAtual.withDayOfMonth(dataAtual.lengthOfMonth());
            parcelasDoMes = visaoGeralRepository.listParcelasDoMes(primeiroDia, ultimoDia);
            nomeMesReferencia = primeiroDia.format(DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("pt-BR")));
        }
        */
        
        double totalParcelasMes = 0.0;
        double totalParcelasPagasMes = 0.0;
        
        for(Parcela p : parcelasDoMes) {
        	totalParcelasMes += p.getValorDevido();
        	if(p.getSituacao().equalsIgnoreCase("paga")) {
        		totalParcelasPagasMes += p.getValorDevido();
        	}
        }
        
        
        
        double arrecadado = valorOuZero(
                visaoGeralRepository.somarArrecadacaoPaga(grupoId));

        double valorNominalGrupo =
                Math.max(grupo.getValorCota(), 0) * (totalCotasGrupo * totalCotasGrupo);

        double percentualReferencia = valorNominalGrupo > 0
                ? Math.min((arrecadado / valorNominalGrupo) * 100.0, 100.0)
                : 0.0;

        double valorContemplacoes =
                (totalContempladas * Math.max(grupo.getValorCota(), 0))*totalCotasGrupo;

        model.addAttribute("arrecadado", arrecadado);
        model.addAttribute("valorContemplacoes", valorContemplacoes);
        model.addAttribute("totalContempladas", totalContempladas);
        model.addAttribute("totalNaoContempladas", totalNaoContempladas);
        model.addAttribute("totalCotasGrupo", totalCotasGrupo);
        model.addAttribute("totalCotasCadastradas", totalCotasCadastradas);
        model.addAttribute("totalParticipantes", participantes.size());
        model.addAttribute("percentualReferencia", percentualReferencia);
        model.addAttribute("valorNominalGrupo", valorNominalGrupo);
        model.addAttribute("listaParticipantes", listaParticipantes);
        model.addAttribute("cotasDto", cotasDto);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("nomeMesReferencia", nomeMesReferencia);
        model.addAttribute("valorDevidoMes", totalParcelasMes);
        model.addAttribute("valorPagoMes", totalParcelasPagasMes);
        

        return "visao-geral";
    }

    private static boolean ehContemplada(String situacao) {
        if (situacao == null) {
            return false;
        }

        String normalizada = Normalizer.normalize(
                situacao.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);

        return normalizada.equals("contemplada")
                || normalizada.equals("contemplado");
    }

    private static String primeiroNome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            return "Participante";
        }

        return nomeCompleto.trim().split("\\s+")[0];
    }

    private static double valorOuZero(Double valor) {
        return valor == null ? 0.0 : valor;
    }

    private static class ResumoParticipante {
        private final String primeiroNome;
        private final Set<Long> idsCotas = new LinkedHashSet<>();
        private boolean temCotaContemplada;

        private ResumoParticipante(String primeiroNome) {
            this.primeiroNome = primeiroNome;
        }
    }
    
}
