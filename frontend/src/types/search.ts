import type { PageResponse } from "./common";
import type { AlbumResponse, ArtistResponse, TrackResponse } from "./media";

export enum SearchType {
    ALBUM = "ALBUM",
    ARTIST = "ARTIST",
    TRACK = "TRACK"
}

export interface SearchResponse {
    albums: PageResponse<AlbumResponse>;
    artists: PageResponse<ArtistResponse>;
    tracks: PageResponse<TrackResponse>;
}
