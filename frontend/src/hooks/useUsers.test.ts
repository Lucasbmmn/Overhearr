import { renderHook, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { useUsers } from './useUsers';
import { userService } from '../services/userService';
import { SortOrder } from '../types/common';
import { UserRole } from '../types/user';
import type { PageResponse } from '../types/common';
import type { User } from '../types/user';

vi.mock('../services/userService', () => ({
    userService: {
        getAllUsers: vi.fn(),
    },
}));

const mockUser: User = {
    id: 'user-1',
    username: 'jdoe',
    email: 'jdoe@example.com',
    role: UserRole.USER,
    createdAt: '2026-01-01T00:00:00Z',
    updatedAt: '2026-01-01T00:00:00Z',
};

const mockPageResponse: PageResponse<User> = {
    data: [mockUser],
    meta: { page: 0, size: 10, totalElements: 1, totalPages: 1 },
};

describe('useUsers', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('should fetch users on mount and return page data', async () => {
        vi.mocked(userService.getAllUsers).mockResolvedValue(mockPageResponse);

        const { result } = renderHook(() => useUsers(0, 10, SortOrder.ASC));

        await waitFor(() => {
            expect(result.current.usersPage).toEqual(mockPageResponse);
        });

        expect(result.current.error).toBeNull();
        expect(userService.getAllUsers).toHaveBeenCalledWith(0, 10, SortOrder.ASC);
    });

    it('should set error when fetch fails', async () => {
        const mockError = new Error('Network error');
        vi.mocked(userService.getAllUsers).mockRejectedValue(mockError);

        const { result } = renderHook(() => useUsers(0, 10, SortOrder.ASC));

        await waitFor(() => {
            expect(result.current.error).toBe(mockError);
        });

        expect(result.current.usersPage).toBeUndefined();
    });

    it('should refetch when calling refetch', async () => {
        vi.mocked(userService.getAllUsers).mockResolvedValue(mockPageResponse);

        const { result } = renderHook(() => useUsers(0, 10, SortOrder.ASC));

        await waitFor(() => {
            expect(result.current.usersPage).toBeDefined();
        });

        vi.mocked(userService.getAllUsers).mockClear();
        const updatedResponse = { ...mockPageResponse, data: [{ ...mockUser, username: 'updated' }] };
        vi.mocked(userService.getAllUsers).mockResolvedValue(updatedResponse);

        await result.current.refetch();

        await waitFor(() => {
            expect(result.current.usersPage?.data[0].username).toBe('updated');
        });
    });

    it('should refetch when page param changes', async () => {
        vi.mocked(userService.getAllUsers).mockResolvedValue(mockPageResponse);

        const { result, rerender } = renderHook(
            ({ page, size, order }) => useUsers(page, size, order),
            { initialProps: { page: 0, size: 10, order: SortOrder.ASC } }
        );

        await waitFor(() => {
            expect(result.current.usersPage).toBeDefined();
        });

        vi.mocked(userService.getAllUsers).mockClear();
        rerender({ page: 1, size: 10, order: SortOrder.ASC });

        await waitFor(() => {
            expect(userService.getAllUsers).toHaveBeenCalledWith(1, 10, SortOrder.ASC);
        });
    });
});
