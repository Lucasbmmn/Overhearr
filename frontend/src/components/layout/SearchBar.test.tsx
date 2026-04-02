import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import SearchBar from './SearchBar';
import { searchService } from '../../services/searchService';
import { MemoryRouter } from 'react-router-dom';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key: string, params?: any) => {
            let result = key;
            if (params && params.query) result = `${key}:${params.query}`;
            return result;
        },
        i18n: { language: 'en' },
    }),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
    const actual = await importOriginal<typeof import('react-router-dom')>();
    return {
        ...actual,
        useNavigate: () => mockNavigate,
    };
});

vi.mock('../../services/searchService', () => ({
    searchService: {
        search: vi.fn(),
    },
}));

describe('SearchBar', () => {
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
        vi.useRealTimers();
    });

    const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

    it('renders search input', () => {
        render(
            <MemoryRouter>
                <SearchBar />
            </MemoryRouter>
        );

        expect(screen.getByPlaceholderText('components.layout.search_bar.search_place_holder')).toBeInTheDocument();
    });

    it('updates input value on change', () => {
        render(
            <MemoryRouter>
                <SearchBar />
            </MemoryRouter>
        );

        const input = screen.getByPlaceholderText('components.layout.search_bar.search_place_holder') as HTMLInputElement;
        fireEvent.change(input, { target: { value: 'queen' } });
        expect(input.value).toBe('queen');
    });

    it('triggers search after debounce delay', async () => {
        vi.mocked(searchService.search).mockResolvedValue(mockResults);

        render(
            <MemoryRouter>
                <SearchBar />
            </MemoryRouter>
        );

        const input = screen.getByPlaceholderText('components.layout.search_bar.search_place_holder');
        fireEvent.change(input, { target: { value: 'queen' } });

        // Wait for debounce delay
        await act(async () => {
            await delay(400);
        });

        await waitFor(() => {
            expect(searchService.search).toHaveBeenCalledWith('queen', [], 0, 3);
        });
    });

    it('displays results in dropdown', async () => {
        vi.mocked(searchService.search).mockResolvedValue(mockResults);

        render(
            <MemoryRouter>
                <SearchBar />
            </MemoryRouter>
        );

        const input = screen.getByPlaceholderText('components.layout.search_bar.search_place_holder');
        fireEvent.focus(input);
        fireEvent.change(input, { target: { value: 'queen' } });

        await act(async () => {
            await delay(400);
        });

        await screen.findByText('Track 1');
        expect(screen.getAllByText('Artist 1').length).toBeGreaterThan(0);
        expect(screen.getByText('Album 1')).toBeInTheDocument();
    });

    it('navigates to SearchPage on enter', () => {
        render(
            <MemoryRouter>
                <SearchBar />
            </MemoryRouter>
        );

        const input = screen.getByPlaceholderText('components.layout.search_bar.search_place_holder');
        fireEvent.change(input, { target: { value: 'queen' } });
        fireEvent.keyUp(input, { key: 'Enter' });

        expect(mockNavigate).toHaveBeenCalled();
        const lastCall = mockNavigate.mock.calls[0][0];
        expect(lastCall).toContain('/search?q=queen');
    });

    it('clears input when X button is clicked', async () => {
        render(
            <MemoryRouter>
                <SearchBar />
            </MemoryRouter>
        );

        const input = screen.getByPlaceholderText('components.layout.search_bar.search_place_holder') as HTMLInputElement;
        fireEvent.change(input, { target: { value: 'queen' } });
        
        const clearButton = screen.getByRole('button');
        fireEvent.click(clearButton);

        expect(input.value).toBe('');
    });
});
