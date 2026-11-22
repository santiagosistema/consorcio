package br.com.santiagopinheiro.controller.web;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import br.com.santiagopinheiro.model.Grupo;
import br.com.santiagopinheiro.repository.GrupoRepository;

@Controller
@RequestMapping("/grupo")
public class GrupoController {
	
	private final GrupoRepository grupoRepository;

	public GrupoController(GrupoRepository grupoRepository) {
		this.grupoRepository = grupoRepository;
	}
	
	
	@GetMapping("/grupo-selecao")
	public String abrirGrupos(
	        @RequestParam(required = false) Long grupoId,
	        Model model) {

	    Grupo grupo = (grupoId != null)
	            ? grupoRepository.findById(grupoId)
	                .orElseThrow(() ->
	                    new ResponseStatusException(
	                        HttpStatus.NOT_FOUND,
	                        "Grupo não encontrado"))
	            : new Grupo();

	    model.addAttribute("grupo", grupo);

	    return "grupo";
	}
	
	
	
	@GetMapping("/selecionar-grupo")
	public String selecionarGrupo(Model model) {

	    model.addAttribute(
	        "grupos",
	        grupoRepository.findAll()
	    );

	    return "selecionar-grupo";
	}
	
	

}
