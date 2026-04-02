import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import SearchPage from './SearchPage';
import { searchService } from '../services/searchService';
import { MemoryRouter, useSearchParams } from 'react-router-dom';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key: string, params?: any) => {
            if (params && params.query) return `${key}:${params.query}`;
            if (params && params.count !== undefined) return `${key}:${params.count}`;
            return key;
        },
        i18n: { language: 'en' },
    }),
}));

vi.mock('react-router-dom', async (importOriginal) => {
    const actual = await importOriginal<typeof import('react-router-dom')>();
    return {
        ...actual,
        useSearchParams: vi.fn(),
    };
});

vi.mock('../services/searchService', () => ({
    searchService: {
        search: vi.fn(),
    },
}));

const mockIntersectionObserver = vi.fn();
mockIntersectionObserver.mockReturnValue({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
});
window.IntersectionObserver = mockIntersectionObserver;

describe('SearchPage', () => {
    const mockResults = {
        tracks: {
            data: [
                { id: 't1', name: 'Track 1', artists: [{ name: 'Artist 1', providerIds: [{ provider: 'P1', id: 'p1' }] }], providerIds: [{ provider: 'P1', id: 'p1' }] }
            ],
            meta: { totalElements: 1, totalPages: 1, page: 0, size: 10 }
        },
        artists: {
            data: [
                { id: 'a1', name: 'Artist 1', providerIds: [{ provider: 'P1', id: 'p1' }] }
            ],
            meta: { totalElements: 1, totalPages: 1, page: 0, size: 10 }
        },
        albums: {
            data: [
                { id: 'al1', title: 'Album 1', artists: [{ name: 'Artist 1', providerIds: [{ provider: 'P1', id: 'p1' }] }], providerIds: [{ provider: 'P1', id: 'p1' }] }
            ],
            meta: { totalElements: 1, totalPages: 1, page: 0, size: 10 }
        }
    };

    beforeEach(() => {
        vi.clearAllMocks();
        vi.mocked(useSearchParams).mockReturnValue([new URLSearchParams('q=queen'), vi.fn()]);
    });

    it('renders and fetches results based on query param', async () => {
        vi.mocked(searchService.search).mockResolvedValue(mockResults);

        render(
            <MemoryRouter>
                <SearchPage />
            </MemoryRouter>
        );

        expect(screen.getByText('search.results_for')).toBeInTheDocument();
        expect(screen.getByText('"queen"')).toBeInTheDocument();

        await screen.findByText('Track 1');
        
        expect(screen.getAllByText('Artist 1').length).toBeGreaterThan(0);
        expect(screen.getByText('Album 1')).toBeInTheDocument();
        
        expect(searchService.search).toHaveBeenCalledWith('queen', [], 0, 25);
    });

    it('changes tab and updates search', async () => {
        vi.mocked(searchService.search).mockResolvedValue(mockResults);
        const setParams = vi.fn();
        vi.mocked(useSearchParams).mockReturnValue([new URLSearchParams('q=queen'), setParams]);

        render(
            <MemoryRouter>
                <SearchPage />
            </MemoryRouter>
        );

        await screen.findByText('Track 1');
        
        const tracksTab = screen.getByRole('button', { name: /search\.tabs\.tracks/i });
        fireEvent.click(tracksTab);

        expect(setParams).toHaveBeenCalled();
    });

    it('handles empty results', async () => {
        vi.mocked(searchService.search).mockResolvedValue({
            tracks: { data: [], meta: { totalElements: 0, totalPages: 0, page: 0, size: 10 } },
            artists: { data: [], meta: { totalElements: 0, totalPages: 0, page: 0, size: 10 } },
            albums: { data: [], meta: { totalElements: 0, totalPages: 0, page: 0, size: 10 } },
        });

        render(
            <MemoryRouter>
                <SearchPage />
            </MemoryRouter>
        );

        const noResults = await screen.findByText('search.no_results');
        expect(noResults).toBeInTheDocument();
    });

    it('deduplicates results correctly', async () => {
        const resultsWithDuplicates = {
            ...mockResults,
            tracks: {
                data: [
                    { id: 't1', name: 'Track 1', artists: [{ name: 'Artist 1', providerIds: [{ provider: 'P1', id: 'p1' }] }], providerIds: [{ provider: 'P1', id: 'p1' }] },
                    { id: 't1', name: 'Track 1', artists: [{ name: 'Artist 1', providerIds: [{ provider: 'P1', id: 'p1' }] }], providerIds: [{ provider: 'P1', id: 'p1' }] }
                ],
                meta: { totalElements: 1, totalPages: 1, page: 0, size: 10 }
            }
        } as any;

        vi.mocked(searchService.search).mockResolvedValue(resultsWithDuplicates);

        render(
            <MemoryRouter>
                <SearchPage />
            </MemoryRouter>
        );

        await waitFor(() => {
            const tracks = screen.getAllByText('Track 1');
            expect(tracks.length).toBe(1);
        });
    });
});
