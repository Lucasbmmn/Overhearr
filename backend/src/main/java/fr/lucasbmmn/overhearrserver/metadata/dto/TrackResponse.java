package fr.lucasbmmn.overhearrserver.metadata.dto;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

/**
 * DTO representing a track entity exposed to API consumers.
 *
 * @param id          Our internal UUID for the track.
 *                    <p>
 *                    Can be null if the track is not yet persisted in our
 *                    system.
 * @param providerIds The list of identifiers for this track across different providers.
 *                    <p>
 *                    Cannot be null.
 * @param name        The title of the track.
 *                    <p>
 *                    Cannot be null.
 * @param artists     The list of the artists of the track.
 *                    <p>
 *                    Cannot be null.
 * @param albums      The list the albums the track belongs to.
 */
public record TrackResponse(
                UUID id,
                @NonNull List<ProviderId> providerIds,
                @NonNull String name,
                @NonNull List<ArtistResponse> artists,
                @NonNull List<AlbumResponse> albums,
                Long durationMs) {
}
