package br.com.santiagopinheiro.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("meuconsorcio")
public class MeuConsorcioController {
	
	@GetMapping({"/",""})
	public String meuConsorcio() {
		
		return "meuconsorcio";
		
	}

}
