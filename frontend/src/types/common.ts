export enum SortOrder {
    ASC = 'ASC',
    DESC = 'DESC',
}

export interface PageMetadata {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export interface PageResponse<T> {
    data: T[];
    meta: PageMetadata;
}