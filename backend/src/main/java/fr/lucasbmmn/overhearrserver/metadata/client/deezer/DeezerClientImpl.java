package fr.lucasbmmn.overhearrserver.metadata.client.deezer;

import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerAlbum;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerArtist;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerSearchResponse;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerTrack;
import fr.lucasbmmn.overhearrserver.metadata.exception.deezer.DeezerSearchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeezerClientImpl implements DeezerClient {
    private final RestClient deezerClient;

    @Override
    public DeezerSearchResponse<DeezerTrack> searchTracks(String query, int limit, int index) {
        return search(query, "track", limit, index, new ParameterizedTypeReference<>() {});
    }

    @Override
    public DeezerSearchResponse<DeezerAlbum> searchAlbums(String query, int limit, int index) {
        return search(query, "album", limit, index, new ParameterizedTypeReference<>() {});
    }

    @Override
    public DeezerSearchResponse<DeezerArtist> searchArtists(String query, int limit, int index) {
        return search(query, "artist", limit, index, new ParameterizedTypeReference<>() {});
    }

    private <T> DeezerSearchResponse<T> search(String query, String type, int limit, int index, ParameterizedTypeReference<DeezerSearchResponse<T>> responseType) {
        try {
            DeezerSearchResponse<T> response = this.deezerClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/{type}")
                            .queryParam("q", query)
                            .queryParam("limit", limit)
                            .queryParam("index", index)
                            .build(type))
                    .retrieve()
                    .body(responseType);

            if (response != null && response.error() != null) {
                throw new DeezerSearchException("Deezer API error: " + response.error().message());
            }

            return response;
        } catch (RestClientException e) {
            throw new DeezerSearchException("Failed to execute Deezer search for " + type, e);
        }
    }
}
