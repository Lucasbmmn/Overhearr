package fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeezerAlbum(
        long id,
        String title,
        String link,
        String cover,
        @JsonProperty("cover_small") String coverSmall,
        @JsonProperty("cover_medium") String coverMedium,
        @JsonProperty("cover_big") String coverBig,
        @JsonProperty("cover_xl") String coverXl,
        DeezerArtist artist,
        String type) {
}
