import type { SearchResponse } from "../types/search";
import type { ProviderId } from "../types/media";

/**
 * Interface for items that can be deduplicated.
 */
export interface Deduplicatable {
    id?: string;
    providerIds: ProviderId[];
}

/**
 * Generates a unique key for a search result item (track, artist, or album).
 * 
 * @param item - The item to generate a key for.
 * @returns A unique string key for the item, or an empty string if no identifiers are found.
 */
export function getResultKey(item: Deduplicatable): string {
    let result = item.id || '';
    if (item.providerIds?.length > 0) {
        result = item.providerIds
            .map(p => `${p.provider}:${p.id}`)
            .sort()
            .join('|');
    }
    return result;
}

/**
 * Removes duplicate items from an array based on their unique keys.
 * 
 * @template T - An object type extending {@link Deduplicatable}.
 * @param arr - The array of items to deduplicate.
 * @returns A new array containing only the first occurrence of each unique item.
 */
export function dedupeArray<T extends Deduplicatable>(arr: T[]): T[] {
    const seen = new Set<string>();
    return arr.filter(item => {
        const key = getResultKey(item);
        let result = true;
        if (key) {
            if (seen.has(key)) {
                result = false;
            } else {
                seen.add(key);
            }
        }
        return result;
    });
}

/**
 * Deduplicates all results within a SearchResponse object.
 * 
 * @param data - The search response to deduplicate.
 * @returns A new SearchResponse with unique entries in all categories.
 */
export function deduplicateResponse(data: SearchResponse): SearchResponse {
    return {
        ...data,
        tracks: { ...data.tracks, data: dedupeArray(data.tracks.data) },
        artists: { ...data.artists, data: dedupeArray(data.artists.data) },
        albums: { ...data.albums, data: dedupeArray(data.albums.data) },
    };
}
