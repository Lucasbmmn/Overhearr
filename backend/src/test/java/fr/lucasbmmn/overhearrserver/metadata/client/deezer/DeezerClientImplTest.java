package fr.lucasbmmn.overhearrserver.metadata.client.deezer;

import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.*;
import fr.lucasbmmn.overhearrserver.metadata.exception.deezer.DeezerSearchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import org.springframework.web.util.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeezerClientImplTest {

    @Mock
    private RestClient restClient;

    @InjectMocks
    private DeezerClientImpl deezerClient;

    @Test
    void searchTracks_shouldReturnTracks() {
        DeezerTrack track = mock(DeezerTrack.class);
        DeezerSearchResponse<DeezerTrack> expectedResponse = new DeezerSearchResponse<>(List.of(track), 1, null, null, null);

        mockRestClientResponse(expectedResponse);

        DeezerSearchResponse<DeezerTrack> response = this.deezerClient.searchTracks("test", 10, 0);

        assertThat(response.data()).hasSize(1);
        assertThat(response.data().getFirst()).isEqualTo(track);
    }

    @Test
    void searchAlbums_shouldReturnAlbums() {
        DeezerAlbum album = mock(DeezerAlbum.class);
        DeezerSearchResponse<DeezerAlbum> expectedResponse = new DeezerSearchResponse<>(List.of(album), 1, null, null, null);

        mockRestClientResponse(expectedResponse);

        DeezerSearchResponse<DeezerAlbum> response = this.deezerClient.searchAlbums("test", 10, 0);

        assertThat(response.data()).hasSize(1);
        assertThat(response.data().getFirst()).isEqualTo(album);
    }

    @Test
    void searchArtists_shouldReturnArtists() {
        DeezerArtist artist = mock(DeezerArtist.class);
        DeezerSearchResponse<DeezerArtist> expectedResponse = new DeezerSearchResponse<>(List.of(artist), 1, null, null, null);

        mockRestClientResponse(expectedResponse);

        DeezerSearchResponse<DeezerArtist> response = this.deezerClient.searchArtists("test", 10, 0);

        assertThat(response.data()).hasSize(1);
        assertThat(response.data().getFirst()).isEqualTo(artist);
    }

    @Test
    void search_shouldThrowException_whenDeezerReturnsError() {
        DeezerError error = new DeezerError("Exception", "Quota limit exceeded", 4, "q");
        DeezerSearchResponse<DeezerAlbum> errorResponse = new DeezerSearchResponse<>(null, 0, null, null, error);

        mockRestClientResponse(errorResponse);

        assertThatThrownBy(() -> this.deezerClient.searchAlbums("test", 10, 0))
                .isInstanceOf(DeezerSearchException.class)
                .hasMessageContaining("Deezer API error: Quota limit exceeded");
    }

    @Test
    void search_shouldThrowException_whenRestClientThrowsException() {
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        doReturn(requestHeadersUriSpec).when(this.restClient).get();
        when(requestHeadersUriSpec.uri(argThat((Function<UriBuilder, URI> f) -> true))).thenThrow(new RestClientException("Connection error"));

        assertThatThrownBy(() -> this.deezerClient.searchTracks("test", 10, 0))
                .isInstanceOf(DeezerSearchException.class)
                .hasMessageContaining("Failed to execute Deezer search for track")
                .hasCauseInstanceOf(RestClientException.class);
    }

    private void mockRestClientResponse(Object response) {
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        doReturn(requestHeadersUriSpec).when(this.restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(argThat((Function<UriBuilder, URI> f) -> true));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(argThat((ParameterizedTypeReference<Object> r) -> true))).thenReturn(response);
    }
}
