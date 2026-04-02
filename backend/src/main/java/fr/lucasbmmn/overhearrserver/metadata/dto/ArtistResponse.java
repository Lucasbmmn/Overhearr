package fr.lucasbmmn.overhearrserver.metadata.dto;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

/**
 * DTO representing an artist entity exposed to API consumers.
 *
 * @param id          Our internal UUID for the artist.
 *                    <p>
 *                    Can be null if the artist is not yet persisted in our
 *                    system.
 * @param providerIds The list of identifiers for this artist across different providers.
 *                    <p>
 *                    Cannot be null.
 * @param name        The title of the artist.
 *                    <p>
 *                    Cannot be null.
 */
public record ArtistResponse(
                UUID id,
                @NonNull List<ProviderId> providerIds,
                @NonNull String name,
                List<ArtistResponse> artists,
                String imageUrl) {
}
