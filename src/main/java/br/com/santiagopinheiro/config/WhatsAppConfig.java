
package br.com.santiagopinheiro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WhatsAppConfig {

    @Bean
    public RestClient whatsappRestClient(

            @Value("${whatsapp.api-version}") String versao,
            @Value("${whatsapp.phone-number-id}") String phoneId,
            @Value("${whatsapp.access-token}") String token) {

        return RestClient.builder()
            .baseUrl(
                "https://graph.facebook.com/"
                + versao + "/" + phoneId
            )
            .defaultHeader(
                "Authorization", "Bearer " + token
            )
            .defaultHeader(
                "Content-Type", "application/json"
            )
            .build();
    }
}

