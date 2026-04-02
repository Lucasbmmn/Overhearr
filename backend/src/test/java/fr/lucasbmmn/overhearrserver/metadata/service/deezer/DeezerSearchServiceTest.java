package fr.lucasbmmn.overhearrserver.metadata.service.deezer;

import fr.lucasbmmn.overhearrserver.metadata.client.deezer.DeezerClient;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerSearchResponse;
import fr.lucasbmmn.overhearrserver.metadata.domain.SearchType;
import fr.lucasbmmn.overhearrserver.metadata.dto.SearchResponse;
import fr.lucasbmmn.overhearrserver.metadata.mapper.DeezerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeezerSearchServiceTest {

    @Mock
    private DeezerMapper deezerMapper;

    @Mock
    private DeezerClient deezerClient;

    @InjectMocks
    private DeezerSearchService deezerSearchService;

    @Test
    void search_CallsDeezerClientForEachType() {
        String query = "test";
        List<SearchType> types = List.of(SearchType.ALBUM, SearchType.ARTIST, SearchType.TRACK);
        int pageNumber = 0;
        int pageSize = 30;
        int index = 0;

        when(this.deezerClient.searchAlbums(anyString(), anyInt(), anyInt())).thenReturn(new DeezerSearchResponse<>(List.of(), 0, null, null, null));
        when(this.deezerClient.searchArtists(anyString(), anyInt(), anyInt())).thenReturn(new DeezerSearchResponse<>(List.of(), 0, null, null, null));
        when(this.deezerClient.searchTracks(anyString(), anyInt(), anyInt())).thenReturn(new DeezerSearchResponse<>(List.of(), 0, null, null, null));
        
        when(this.deezerMapper.toSearchResponse(any(), any(), any(), anyInt(), anyInt())).thenReturn(mock(SearchResponse.class));

        this.deezerSearchService.search(query, types, pageNumber, pageSize);

        verify(this.deezerClient).searchAlbums(query, pageSize, index);
        verify(this.deezerClient).searchArtists(query, pageSize, index);
        verify(this.deezerClient).searchTracks(query, pageSize, index);
        verify(this.deezerMapper).toSearchResponse(any(), any(), any(), eq(pageNumber), eq(pageSize));
    }

    @Test
    void search_CallsOnlyRequestedTypes() {
        String query = "test";
        List<SearchType> types = List.of(SearchType.ALBUM);
        int pageNumber = 1;
        int pageSize = 10;
        int index = 10;

        when(this.deezerClient.searchAlbums(anyString(), anyInt(), anyInt())).thenReturn(new DeezerSearchResponse<>(List.of(), 0, null, null, null));
        when(this.deezerMapper.toSearchResponse(any(), isNull(), isNull(), anyInt(), anyInt())).thenReturn(mock(SearchResponse.class));

        this.deezerSearchService.search(query, types, pageNumber, pageSize);

        verify(this.deezerClient).searchAlbums(query, pageSize, index);
        verify(this.deezerClient, never()).searchArtists(anyString(), anyInt(), anyInt());
        verify(this.deezerClient, never()).searchTracks(anyString(), anyInt(), anyInt());
    }
}
