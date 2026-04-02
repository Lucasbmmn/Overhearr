import { describe, it, expect } from 'vitest';
import { getResultKey, dedupeArray, deduplicateResponse } from './searchUtils';

describe('searchUtils', () => {
    describe('getResultKey', () => {
        it('uses providerIds if present', () => {
            const item = {
                id: 'local-id',
                providerIds: [
                    { provider: 'musicbrainz', id: 'm1' },
                    { provider: 'deezer', id: 'd1' }
                ]
            };
            expect(getResultKey(item)).toBe('deezer:d1|musicbrainz:m1');
        });

        it('falls back to id if providerIds is empty', () => {
            const item = {
                id: 'local-id',
                providerIds: []
            };
            expect(getResultKey(item)).toBe('local-id');
        });

        it('returns empty string if no id or providerIds', () => {
            const item = {
                providerIds: []
            };
            expect(getResultKey(item)).toBe('');
        });
    });

    describe('dedupeArray', () => {
        it('removes duplicates based on getResultKey', () => {
            const items = [
                { id: '1', providerIds: [{ provider: 'p1', id: '1' }], name: 'Item 1' },
                { id: '2', providerIds: [{ provider: 'p1', id: '1' }], name: 'Item 1 Duplicate' },
                { id: '3', providerIds: [{ provider: 'p2', id: '3' }], name: 'Item 2' }
            ];
            const result = dedupeArray(items);
            expect(result).toHaveLength(2);
            expect(result[0].id).toBe('1');
            expect(result[1].id).toBe('3');
        });

        it('handles empty arrays', () => {
            expect(dedupeArray([])).toEqual([]);
        });
    });

    describe('deduplicateResponse', () => {
        it('deduplicates all sections of a SearchResponse', () => {
            const mockResponse = {
                tracks: {
                    data: [
                        { id: 't1', providerIds: [{ provider: 'p1', id: 't1' }], name: 'Track 1' },
                        { id: 't1', providerIds: [{ provider: 'p1', id: 't1' }], name: 'Track 1 Duplicate' }
                    ],
                    meta: { totalElements: 2, totalPages: 1, page: 0, size: 10 }
                },
                artists: {
                    data: [
                        { id: 'a1', providerIds: [{ provider: 'p1', id: 'a1' }], name: 'Artist 1' }
                    ],
                    meta: { totalElements: 1, totalPages: 1, page: 0, size: 10 }
                },
                albums: {
                    data: [
                        { id: 'al1', providerIds: [{ provider: 'p1', id: 'al1' }], title: 'Album 1' },
                        { id: 'al1', providerIds: [{ provider: 'p1', id: 'al1' }], title: 'Album 1 Duplicate' }
                    ],
                    meta: { totalElements: 2, totalPages: 1, page: 0, size: 10 }
                }
            };

            const result = deduplicateResponse(mockResponse as any);
            expect(result.tracks.data).toHaveLength(1);
            expect(result.artists.data).toHaveLength(1);
            expect(result.albums.data).toHaveLength(1);
        });
    });
});
