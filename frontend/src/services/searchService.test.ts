import { describe, it, expect, vi, beforeEach } from 'vitest';
import { searchService } from './searchService';
import api from '../lib/axios';
import { SearchType } from '../types/search';

vi.mock('../lib/axios', () => ({
    default: {
        get: vi.fn(),
    },
}));

describe('searchService', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('should call api.get with correct parameters for a simple query', async () => {
        const mockResponse = { data: { albums: { data: [], meta: {} }, artists: { data: [], meta: {} }, tracks: { data: [], meta: {} } } };
        vi.mocked(api.get).mockResolvedValue(mockResponse);

        const query = 'test query';
        await searchService.search(query);

        expect(api.get).toHaveBeenCalledWith('/search', {
            params: {
                q: query,
                pageNumber: 0,
                pageSize: 10,
            },
        });
    });

    it('should call api.get with correct parameters when types are provided', async () => {
        const mockResponse = { data: { albums: { data: [], meta: {} }, artists: { data: [], meta: {} }, tracks: { data: [], meta: {} } } };
        vi.mocked(api.get).mockResolvedValue(mockResponse);

        const query = 'test query';
        const types = [SearchType.TRACK, SearchType.ALBUM];
        await searchService.search(query, types);

        expect(api.get).toHaveBeenCalledWith('/search', {
            params: {
                q: query,
                type: 'TRACK,ALBUM',
                pageNumber: 0,
                pageSize: 10,
            },
        });
    });

    it('should call api.get with correct pagination parameters', async () => {
        const mockResponse = { data: { albums: { data: [], meta: {} }, artists: { data: [], meta: {} }, tracks: { data: [], meta: {} } } };
        vi.mocked(api.get).mockResolvedValue(mockResponse);

        const query = 'test query';
        await searchService.search(query, [], 2, 20);

        expect(api.get).toHaveBeenCalledWith('/search', {
            params: {
                q: query,
                pageNumber: 2,
                pageSize: 20,
            },
        });
    });

    it('should return response data', async () => {
        const mockData = { 
            albums: { data: [], meta: { page: 0, size: 10, totalElements: 0, totalPages: 0 } }, 
            artists: { data: [], meta: { page: 0, size: 10, totalElements: 0, totalPages: 0 } }, 
            tracks: { data: [], meta: { page: 0, size: 10, totalElements: 0, totalPages: 0 } } 
        };
        vi.mocked(api.get).mockResolvedValue({ data: mockData });

        const result = await searchService.search('test');

        expect(result).toEqual(mockData);
    });
});
