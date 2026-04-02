package fr.lucasbmmn.overhearrserver.metadata.mapper;

import fr.lucasbmmn.overhearrserver.common.dto.PageResponse;
import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.*;
import fr.lucasbmmn.overhearrserver.metadata.domain.Provider;
import fr.lucasbmmn.overhearrserver.metadata.domain.SearchType;
import fr.lucasbmmn.overhearrserver.metadata.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {Provider.class, ProviderId.class, List.class})
public interface DeezerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "providerIds", expression = "java(toProviderIds(deezerArtist.id()))")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "imageUrl", source = "pictureMedium")
    ArtistResponse toArtistResponse(DeezerArtist deezerArtist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "providerIds", expression = "java(toProviderIds(deezerAlbum.id()))")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "artists", expression = "java(deezerAlbum.artist() != null ? List.of(toArtistResponse(deezerAlbum.artist())) : List.of())")
    @Mapping(target = "coverArtUrl", source = "coverMedium")
    AlbumResponse toAlbumResponse(DeezerAlbum deezerAlbum);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "providerIds", expression = "java(toProviderIds(deezerTrack.id()))")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "artists", expression = "java(deezerTrack.artist() != null ? List.of(toArtistResponse(deezerTrack.artist())) : List.of())")
    @Mapping(target = "albums", expression = "java(deezerTrack.album() != null ? List.of(toAlbumResponse(deezerTrack.album())) : List.of())")
    @Mapping(target = "durationMs", expression = "java(deezerTrack.duration() * 1000L)")
    TrackResponse toTrackResponse(DeezerTrack deezerTrack);

    default List<ProviderId> toProviderIds(long id) {
        return List.of(new ProviderId(Provider.DEEZER, String.valueOf(id)));
    }

    default SearchResponse toSearchResponse(
            DeezerSearchResponse<DeezerAlbum> albumsResponse,
            DeezerSearchResponse<DeezerArtist> artistsResponse,
            DeezerSearchResponse<DeezerTrack> tracksResponse,
            int pageNumber,
            int pageSize) {

        List<AlbumResponse> albums = albumsResponse != null && albumsResponse.data() != null
                ? albumsResponse.data().stream().map(this::toAlbumResponse).toList()
                : List.of();

        List<ArtistResponse> artists = artistsResponse != null && artistsResponse.data() != null
                ? artistsResponse.data().stream().map(this::toArtistResponse).toList()
                : List.of();

        List<TrackResponse> tracks = tracksResponse != null && tracksResponse.data() != null
                ? tracksResponse.data().stream().map(this::toTrackResponse).toList()
                : List.of();

        return new SearchResponse(
                new PageResponse<>(albums, createMetadata(albumsResponse, pageNumber, pageSize)),
                new PageResponse<>(artists, createMetadata(artistsResponse, pageNumber, pageSize)),
                new PageResponse<>(tracks, createMetadata(tracksResponse, pageNumber, pageSize))
        );
    }

    private PageResponse.PageMetadata createMetadata(DeezerSearchResponse<?> response, int pageNumber, int pageSize) {
        int totalElements = response != null ? response.total() : 0;
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return new PageResponse.PageMetadata(pageNumber, pageSize, totalElements, totalPages);
    }
}
