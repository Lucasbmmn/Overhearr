package fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeezerArtist(
        long id,
        String name,
        String link,
        String picture,
        @JsonProperty("picture_small") String pictureSmall,
        @JsonProperty("picture_medium") String pictureMedium,
        @JsonProperty("picture_big") String pictureBig,
        @JsonProperty("picture_xl") String pictureXl,
        String type) {
}
