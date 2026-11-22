package br.com.santiagopinheiro.controller.api;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.santiagopinheiro.model.Grupo;
import br.com.santiagopinheiro.repository.GrupoRepository;

@RestController
@RequestMapping("/api/grupo")
public class GrupoRestController {
	
	private GrupoRepository grupoRepository;
	
	public GrupoRestController(GrupoRepository grupoRepository) {
		this.grupoRepository = grupoRepository;
	}
	
	@PostMapping("/salvar")
	public ResponseEntity<Grupo> salvar(@RequestBody Grupo grupo){
		
		//salva um grupo novo
		if(grupo.getId() == null || grupo.getId() == 0) {
			
			LocalDate dataAtual = LocalDate.now();
			
			grupo.setDataSituacao(dataAtual);
			
			// Calculo da data fim passo a passo
			int cotas = grupo.getTotalCotas()-1;
			LocalDate dataInicio = grupo.getDataInicio();
			LocalDate dataFim = dataInicio.plusMonths(cotas);
			grupo.setDataFim(dataFim);
			
		
			Grupo grupoSalvo = grupoRepository.save(grupo);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(grupoSalvo);

		} else {
			
			//atualiza um grupo
			Grupo grupoSalvo = grupoRepository.save(grupo);
			return ResponseEntity.status(HttpStatus.CREATED).body(grupoSalvo);
			
		}
		
		
	}//salvar
	
	
	@GetMapping("/{id}/quantidade-cotas")
	public ResponseEntity<Integer> buscarQuantidadeCotas(@PathVariable Long id) {

	    return grupoRepository.findById(id)
	            .map(grupo -> ResponseEntity.ok(grupo.getTotalCotas()))
	            .orElse(ResponseEntity.notFound().build());
	    
	}//buscarQuantidadeCotas
	
	
	
	
	
}
