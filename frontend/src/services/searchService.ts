import api from "../lib/axios";
import type { SearchResponse, SearchType } from "../types/search";

export const searchService = {
    async search(
        query: string,
        type: SearchType[] = [],
        pageNumber: number = 0,
        pageSize: number = 10
    ): Promise<SearchResponse> {
        const typeParams = type.length > 0 ? type.join(",") : undefined;
        const response = await api.get<SearchResponse>('/search', {
            params: {
                q: query,
                ...(typeParams && { type: typeParams }),
                pageNumber,
                pageSize
            }
        });
        return response.data;
    }
};
