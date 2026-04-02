package fr.lucasbmmn.overhearrserver.metadata.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final DeezerProperties deezerProperties;
    
    @Bean
    public RestClient deezerClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.clone()
                .baseUrl(this.deezerProperties.baseUrl())
                .build();
    }
}
