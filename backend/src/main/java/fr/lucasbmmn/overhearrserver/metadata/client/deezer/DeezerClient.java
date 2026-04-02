package fr.lucasbmmn.overhearrserver.metadata.client.deezer;

import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerAlbum;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerArtist;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerSearchResponse;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerTrack;

public interface DeezerClient {
    DeezerSearchResponse<DeezerTrack> searchTracks(String query, int limit, int index);

    DeezerSearchResponse<DeezerAlbum> searchAlbums(String query, int limit, int index);

    DeezerSearchResponse<DeezerArtist> searchArtists(String query, int limit, int index);
}
