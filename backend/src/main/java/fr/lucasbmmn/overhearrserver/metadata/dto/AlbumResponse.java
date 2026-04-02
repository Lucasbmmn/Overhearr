package fr.lucasbmmn.overhearrserver.metadata.dto;
 
import lombok.NonNull;
 
import java.util.List;
import java.util.UUID;
 
/**
 * DTO representing an album entity exposed to API consumers.
 *
 * @param id          Our internal UUID for the album.
 *                    <p>
 *                    Can be null if the album is not yet persisted in our
 *                    system.
 * @param providerIds The list of identifiers for this album across different providers.
 *                    <p>
 *                    Cannot be null.
 * @param title       The title/name of the album.
 *                    <p>
 *                    Cannot be null.
 * @param artists     The list of the artists of the album.
 *                    <p>
 *                    Cannot be null.
 * @param releaseDate The date the album was released (e.g. "2023-05-15").
 * @param coverArtUrl    The URL for the album's cover image.
 */
public record AlbumResponse(
                UUID id,
                @NonNull List<ProviderId> providerIds,
                @NonNull String title,
                List<ArtistResponse> artists,
                String releaseDate,
                String coverArtUrl) {
}
