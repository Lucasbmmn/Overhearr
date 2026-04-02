package fr.lucasbmmn.overhearrserver.metadata.mapper;

import fr.lucasbmmn.overhearrserver.metadata.client.deezer.dto.*;
import fr.lucasbmmn.overhearrserver.metadata.domain.Provider;
import fr.lucasbmmn.overhearrserver.metadata.dto.AlbumResponse;
import fr.lucasbmmn.overhearrserver.metadata.dto.ArtistResponse;
import fr.lucasbmmn.overhearrserver.metadata.dto.SearchResponse;
import fr.lucasbmmn.overhearrserver.metadata.dto.TrackResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeezerMapperTest {

    private DeezerMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = new DeezerMapperImpl();
    }

    @Test
    void shouldMapArtist() {
        DeezerArtist artist = new DeezerArtist(12345L, "Daft Punk", "http://deezer.com/artist/123", "http://picture.jpg", "http://small.jpg", "http://medium.jpg", "http://big.jpg", "http://xl.jpg", "artist");

        ArtistResponse response = this.mapper.toArtistResponse(artist);

        assertThat(response.name()).isEqualTo("Daft Punk");
        assertThat(response.imageUrl()).isEqualTo("http://medium.jpg");
        assertThat(response.providerIds()).hasSize(1);
        assertThat(response.providerIds().getFirst().provider()).isEqualTo(Provider.DEEZER);
        assertThat(response.providerIds().getFirst().id()).isEqualTo("12345");
    }

    @Test
    void shouldMapAlbum() {
        DeezerArtist artist = new DeezerArtist(12345L, "Daft Punk", "http://artist", "http://picture", "http://small", "http://medium", "http://big", "http://xl", "artist");
        DeezerAlbum album = new DeezerAlbum(67890L, "Discovery", "http://deezer.com/album/456", "http://cover.jpg", "http://cs.jpg", "http://cm.jpg", "http://cb.jpg", "http://cx.jpg", artist, "album");

        AlbumResponse response = this.mapper.toAlbumResponse(album);

        assertThat(response.title()).isEqualTo("Discovery");
        assertThat(response.coverArtUrl()).isEqualTo("http://cm.jpg");
        assertThat(response.artists()).hasSize(1);
        assertThat(response.artists().getFirst().name()).isEqualTo("Daft Punk");
        assertThat(response.providerIds()).hasSize(1);
        assertThat(response.providerIds().getFirst().provider()).isEqualTo(Provider.DEEZER);
        assertThat(response.providerIds().getFirst().id()).isEqualTo("67890");
    }

    @Test
    void shouldMapTrack() {
        DeezerTrack track = this.getDeezerTrack();

        TrackResponse response = this.mapper.toTrackResponse(track);

        assertThat(response.name()).isEqualTo("One More Time");
        assertThat(response.durationMs()).isEqualTo(320000L);
        assertThat(response.artists()).hasSize(1);
        assertThat(response.artists().getFirst().name()).isEqualTo("Daft Punk");
        assertThat(response.albums()).hasSize(1);
        assertThat(response.albums().getFirst().title()).isEqualTo("Discovery");
        assertThat(response.providerIds()).hasSize(1);
        assertThat(response.providerIds().getFirst().provider()).isEqualTo(Provider.DEEZER);
        assertThat(response.providerIds().getFirst().id()).isEqualTo("11111");
    }

    @Test
    void shouldMapSearchResponse() {
        DeezerArtist artist = new DeezerArtist(1L, "Artist", "url", "pic", "s", "m", "b", "xl", "artist");
        DeezerAlbum album = new DeezerAlbum(2L, "Album", "url", "cov", "s", "m", "b", "xl", artist, "album");
        DeezerTrack track = new DeezerTrack(3L, "Track", "Track", "url", 180, 500, false, "prev", artist, album, "track");

        DeezerSearchResponse<DeezerAlbum> albumsResp = new DeezerSearchResponse<>(List.of(album), 1, null, null, null);
        DeezerSearchResponse<DeezerArtist> artistsResp = new DeezerSearchResponse<>(List.of(artist), 1, null, null, null);
        DeezerSearchResponse<DeezerTrack> tracksResp = new DeezerSearchResponse<>(List.of(track), 1, null, null, null);

        SearchResponse response = this.mapper.toSearchResponse(albumsResp, artistsResp, tracksResp, 0, 10);

        assertThat(response.albums().data()).hasSize(1);
        assertThat(response.artists().data()).hasSize(1);
        assertThat(response.tracks().data()).hasSize(1);
        assertThat(response.albums().meta().totalElements()).isEqualTo(1);
        assertThat(response.albums().meta().page()).isEqualTo(0);
    }

    @Test
    void shouldHandleNulls() {
        DeezerAlbum album = new DeezerAlbum(67890L, "Discovery", null, null, null, null, null, null, null, "album");
        DeezerTrack track = new DeezerTrack(11111L, "One More Time", null, null, 320, 1000, false, null, null, null, "track");

        AlbumResponse albumResponse = this.mapper.toAlbumResponse(album);
        TrackResponse trackResponse = this.mapper.toTrackResponse(track);

        assertThat(albumResponse.artists()).isEmpty();
        assertThat(trackResponse.artists()).isEmpty();
        assertThat(trackResponse.albums()).isEmpty();
    }

    private DeezerTrack getDeezerTrack() {
        DeezerArtist artist = new DeezerArtist(12345L, "Daft Punk", "http://artist", "http://picture", "http://small", "http://medium", "http://big", "http://xl", "artist");
        DeezerAlbum album = new DeezerAlbum(67890L, "Discovery", "http://album", "http://cover", "http://cs", "http://cm", "http://cb", "http://cx", artist, "album");
        return new DeezerTrack(11111L, "One More Time", "One More Time", "http://deezer.com/track/789", 320, 1000, false, "http://preview.mp3", artist, album, "track");
    }
}
