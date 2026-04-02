package fr.lucasbmmn.overhearrserver.metadata.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "overhearr.providers.deezer")
public record DeezerProperties(
        String baseUrl) {
}
