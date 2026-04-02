package fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto;

import java.util.List;

public record DeezerSearchResponse<T>(
        List<T> data,
        int total,
        String next,
        String prev,
        DeezerError error) {
}
