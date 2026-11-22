
package br.com.santiagopinheiro.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class WhatsAppService {

    private final RestClient whatsappRestClient;

    public WhatsAppService(RestClient whatsappRestClient) {
        this.whatsappRestClient = whatsappRestClient;
    }

    /**
     * Envia uma mensagem de teste utilizando
     * o template hello_world da Meta.
     *
     * O telefone deve estar no formato internacional,
     * somente com números.
     */
    public void testarEnvio(String telefone) {

        Map<String, Object> dados = Map.of(
            "messaging_product", "whatsapp",
            "to", telefone,
            "type", "template",
            "template", Map.of(
                "name", "hello_world",
                "language", Map.of(
                    "code", "en_US"
                )
            )
        );

        enviarRequisicao(dados);
    }

    /**
     * Envia o template de lembrete de vencimento.
     * Este template deverá estar aprovado na Meta.
     */
    public void enviarLembrete(
            String telefone,
            String nome,
            String dataVencimento,
            String valor) {

        Map<String, Object> dados = Map.of(
            "messaging_product", "whatsapp",
            "to", telefone,
            "type", "template",
            "template", Map.of(
                "name", "lembrete_vencimento",
                "language", Map.of(
                    "code", "pt_BR"
                ),
                "components", List.of(

                    // Cabeçalho: nome do participante
                    Map.of(
                        "type", "header",
                        "parameters", List.of(
                            parametro(nome)
                        )
                    ),

                    // Corpo: data e valor da parcela
                    Map.of(
                        "type", "body",
                        "parameters", List.of(
                            parametro(dataVencimento),
                            parametro(valor)
                        )
                    )
                )
            )
        );

        enviarRequisicao(dados);
    }
    
    
    
    
    public void notificaContemplacao(String telefone) {
    	Map<String, Object> dados = Map.of(
    			 "messaging_product", "whatsapp",
    			 "to", telefone,
    			 "type", "template",
    			 "template", Map.of("name", "aviso_contemplacao",
    					 "language", Map.of(
    							 "code", "pt_BR"
    							 	)
    					 )
    			 );
    	
    	enviarRequisicao(dados);
    }

    
    
    
    /**
     * Executa a chamada HTTP para a API da Meta.
     */
    private void enviarRequisicao(Map<String, Object> dados) {

        try {

            whatsappRestClient.post()
                .uri("/messages")
                .body(dados)
                .retrieve()
                .toBodilessEntity();

        } catch (RestClientResponseException e) {

            throw new RuntimeException(
                "Erro ao enviar WhatsApp. HTTP: "
                + e.getStatusCode()
                + " - "
                + e.getResponseBodyAsString(),
                e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                "Falha na comunicação com a API do WhatsApp: "
                + e.getMessage(),
                e
            );
        }
    }

    /**
     * Monta os parâmetros de texto do template.
     */
    private Map<String, String> parametro(String valor) {

        return Map.of(
            "type", "text",
            "text", valor
        );
    }
    
}
