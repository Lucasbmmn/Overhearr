package fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeezerTrack(
        long id,
        String title,
        @JsonProperty("title_short") String titleShort,
        String link,
        int duration,
        int rank,
        @JsonProperty("explicit_lyrics") boolean explicitLyrics,
        String preview,
        DeezerArtist artist,
        DeezerAlbum album,
        String type) {
}
