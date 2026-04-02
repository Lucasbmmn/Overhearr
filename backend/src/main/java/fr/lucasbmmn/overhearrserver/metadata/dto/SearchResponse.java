package fr.lucasbmmn.overhearrserver.metadata.dto;

import fr.lucasbmmn.overhearrserver.common.dto.PageResponse;

/**
 * DTO representing the response for a search query, exposed to API consumers.
 *
 * @param albums  A paginated list of albums matching the search query.
 *                <p>
 *                Cannot be null.
 * @param artists A paginated list of artists matching the search query.
 *                <p>
 *                Cannot be null.
 * @param tracks  A paginated list of tracks matching the search query.
 *                <p>
 *                Cannot be null.
 */
public record SearchResponse(
        PageResponse<AlbumResponse> albums,
        PageResponse<ArtistResponse> artists,
        PageResponse<TrackResponse> tracks) {
}
