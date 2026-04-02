package fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto;

public record DeezerError(
        String type,
        String message,
        int code,
        String parameter) {
}
