
package br.com.santiagopinheiro.teste;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.santiagopinheiro.service.WhatsAppService;

//@Component
public class WhatsAppTeste implements CommandLineRunner {

    private final WhatsAppService whatsAppService;

    public WhatsAppTeste(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    @Override
    public void run(String... args) throws Exception {

        try {

            System.out.println("Iniciando teste WhatsApp...");

            whatsAppService.enviarLembrete(
                "5568992039934",  // Telefone com código do país e DDD
                "Santiago",       // Nome do participante
                "10/11/2026",     // Data de vencimento
                "R$ 300,00"       // Valor da parcela
            );

            System.out.println("Solicitação enviada para a Meta!");

        } catch (Exception e) {

            System.err.println("Erro no teste WhatsApp:");
            e.printStackTrace();
        }
    }
}
