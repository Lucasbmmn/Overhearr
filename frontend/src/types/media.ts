export interface ProviderId {
    provider: string;
    id: string;
}

export interface AlbumResponse {
    id?: string;
    providerIds: ProviderId[];
    title: string;
    artists?: ArtistResponse[];
    coverArtUrl?: string;
    releaseDate?: string;
}

export interface ArtistResponse {
    id?: string;
    providerIds: ProviderId[];
    name: string;
    imageUrl?: string;
}

export interface TrackResponse {
    id?: string;
    providerIds: ProviderId[];
    name: string;
    durationMs?: number;
    artists: ArtistResponse[];
    albums?: AlbumResponse[];
}