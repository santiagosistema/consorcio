
package br.com.santiagopinheiro.controller.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.santiagopinheiro.model.Cota;
import br.com.santiagopinheiro.model.CotaParticipante;
import br.com.santiagopinheiro.model.Grupo;
import br.com.santiagopinheiro.model.Usuario;
import br.com.santiagopinheiro.repository.CotaParticipanteRepository;
import br.com.santiagopinheiro.repository.CotaRepository;
import br.com.santiagopinheiro.repository.GrupoRepository;
import br.com.santiagopinheiro.repository.UsuarioRepository;
import br.com.santiagopinheiro.service.EmailsTodosService;

@Controller
public class CotaController {

    private final CotaRepository cotaRepository;
    private final GrupoRepository grupoRepository;
    private final CotaParticipanteRepository cotaParticipanteRepository;
    private final EmailsTodosService email;
    private final UsuarioRepository usuarioRepository;

    public CotaController(
            CotaRepository cotaRepository,
            GrupoRepository grupoRepository,
            CotaParticipanteRepository cotaParticipanteRepository,
            EmailsTodosService email,
            UsuarioRepository usuarioRepository) {

        this.cotaRepository = cotaRepository;
        this.grupoRepository = grupoRepository;
        this.cotaParticipanteRepository = cotaParticipanteRepository;
        this.email = email;
        this.usuarioRepository = usuarioRepository;
    }
    
    

    // Abre a página de distribuição de cotas
    @GetMapping("/cota")
    public String cota(Model model) {

        List<Grupo> grupos = grupoRepository.findAll();

        model.addAttribute("grupos", grupos);

        return "cota";
    }

    // Abre a página de consulta da distribuição de cotas
    @GetMapping("/cotas/consultar")
    public String consultarCotas(Model model) {

        List<Grupo> grupos = grupoRepository.findAll();

        model.addAttribute("grupos", grupos);

        return "listar-cotas";
    }

 // Consulta a distribuição de cotas de um grupo
    @GetMapping("/api/cota/grupo/{grupoId}/distribuicao")
    public ResponseEntity<?> listarDistribuicao(@PathVariable Long grupoId) {

        List<Cota> cotas = cotaRepository
                .findByGrupo_IdOrderByNumeroCotaAsc(grupoId);

        List<CotaDistribuicaoDTO> resultado = cotas.stream()
                .map(cota -> {

                    List<ParticipanteDistribuicaoDTO> participantes =
                            cotaParticipanteRepository
                                    .findByCota_IdOrderByUsuario_NomeAsc(cota.getId())
                                    .stream()
                                    .map(cp -> new ParticipanteDistribuicaoDTO(
                                            cp.getUsuario().getId(),
                                            cp.getUsuario().getNome(),
                                            cp.getPercentualParticipacao()
                                    ))
                                    .toList();

                    return new CotaDistribuicaoDTO(
                            cota.getId(),
                            cota.getNumeroCota(),
                            cota.getSituacaoCota(),
                            participantes
                    );
                })
                .toList();

        return ResponseEntity.ok(resultado);
    }
    
    

    public record CotaDistribuicaoDTO(
            Long id,
            Integer numeroCota,
            String situacaoCota,
            List<ParticipanteDistribuicaoDTO> participantes) {
    }

    public record ParticipanteDistribuicaoDTO(
            Long usuarioId,
            String nome,
            BigDecimal percentual) {
    }
    
    
    
    @PutMapping("/api/cota/{cotaId}/situacao")
    public ResponseEntity<?> alterarSituacaoCota(
            @PathVariable Long cotaId,
            @RequestBody AlterarSituacaoDTO dados) {

        if (dados == null || dados.situacaoCota() == null) {
            return ResponseEntity.badRequest()
                    .body("Informe a situação da cota.");
        }

        String situacao = dados.situacaoCota()
                .trim()
                .toUpperCase(java.util.Locale.ROOT);

        if (!List.of("ATIVA", "CONTEMPLADA", "CANCELADA")
                .contains(situacao)) {
            return ResponseEntity.badRequest()
                    .body("Situação inválida.");
        }

        Cota cota = cotaRepository.findById(cotaId)
                .orElse(null);

        if (cota == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cota não encontrada.");
        }
        
        //COLOCAR AQUI O MÉTODO DO EMAIL
        
		if (situacao.equalsIgnoreCase("contemplada")) {
			
			List<CotaParticipante> cotaParticipantes = cotaParticipanteRepository
					.findByCota_IdOrderByUsuario_NomeAsc(cotaId);
			
			email.emailContemplacao(cota, cotaParticipantes);
			
			Grupo grupo = cota.getGrupo();
			
			List<Usuario> usuarios = cotaParticipanteRepository.findUserbyGrupoId(grupo.getId());
			
			email.emailNotificaTodosContemplacao(usuarios);
			
		}
        

        cota.setSituacaoCota(situacao);
        cotaRepository.save(cota);

        return ResponseEntity.ok(
                new CotaSituacaoDTO(cota.getId(), cota.getSituacaoCota())
        );
    }

    public record AlterarSituacaoDTO(String situacaoCota) {
    }

    public record CotaSituacaoDTO(Long id, String situacaoCota) {
    }
}
