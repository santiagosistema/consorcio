package br.com.santiagopinheiro.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoScheduler {
	
	private final NotificacaoService notificacaoService;
	
	public NotificacaoScheduler(NotificacaoService notificacaoService) {
		this.notificacaoService = notificacaoService;
	}
	
	
	@Scheduled(cron = "0 0 7 * * *", zone = "America/Rio_Branco")
	public void verificarNotificacoes() {
		notificacaoService.verificarParcelasAtrasadas();
	}
	
	
	@Scheduled(cron = "0 0 8 1-5 * *", zone = "America/Rio_Branco")
	public void verificarVencimento() {
		notificacaoService.parcelasProximasDoVencimento();
	}
	
	
	// Aqui o envio acontece quando o sistema sobe.
	//@EventListener(ApplicationReadyEvent.class)
	public void executarAoIniciar() {
		notificacaoService.verificarParcelasAtrasadas();
	}
	
	
}
