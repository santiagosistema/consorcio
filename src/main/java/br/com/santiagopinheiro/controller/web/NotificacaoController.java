package br.com.santiagopinheiro.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificacaoController {

	@GetMapping("/notificacoes")
	public String listarNotificacoes() {
		return "notificacoes";
	}
}
