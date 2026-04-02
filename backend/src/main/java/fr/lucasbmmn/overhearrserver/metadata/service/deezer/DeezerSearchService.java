package fr.lucasbmmn.overhearrserver.metadata.service.deezer;

import fr.lucasbmmn.overhearrserver.metadata.client.deezer.DeezerClient;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerAlbum;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerArtist;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerSearchResponse;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.DeezerTrack;
import fr.lucasbmmn.overhearrserver.metadata.domain.SearchType;
import fr.lucasbmmn.overhearrserver.metadata.dto.SearchResponse;
import fr.lucasbmmn.overhearrserver.metadata.mapper.DeezerMapper;
import fr.lucasbmmn.overhearrserver.metadata.service.MetadataSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeezerSearchService implements MetadataSearchService {
    private final DeezerMapper deezerMapper;
    private final DeezerClient deezerClient;

    @Override
    public SearchResponse search(String query, List<SearchType> searchTypes, int pageNumber, int pageSize) {
        DeezerSearchResponse<DeezerAlbum> albumsResponse = null;
        DeezerSearchResponse<DeezerArtist> artistsResponse = null;
        DeezerSearchResponse<DeezerTrack> tracksResponse = null;

        int index = pageNumber * pageSize;

        for (SearchType type : searchTypes) {
            switch (type) {
                case ALBUM -> albumsResponse = this.deezerClient.searchAlbums(query, pageSize, index);
                case ARTIST -> artistsResponse = this.deezerClient.searchArtists(query, pageSize, index);
                case TRACK -> tracksResponse = this.deezerClient.searchTracks(query, pageSize, index);
            }
        }

        return this.deezerMapper.toSearchResponse(albumsResponse, artistsResponse, tracksResponse, pageNumber, pageSize);
    }
}
