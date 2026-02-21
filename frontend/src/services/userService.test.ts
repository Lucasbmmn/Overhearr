import { describe, it, expect, vi, beforeEach } from 'vitest';
import { userService } from './userService';
import api from '../lib/axios';
import { SortOrder } from '../types/common';
import { UserRole } from '../types/user';
import type { User, UserCreationRequest, AdminUserUpdateRequest, PasswordUpdateRequest } from '../types/user';
import type { PageResponse } from '../types/common';

vi.mock('../lib/axios', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
        put: vi.fn(),
        patch: vi.fn(),
        delete: vi.fn(),
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

describe('userService', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    describe('getAllUsers', () => {
        it('should call api.get with default params and return page response', async () => {
            vi.mocked(api.get).mockResolvedValue({ data: mockPageResponse });

            const result = await userService.getAllUsers();

            expect(api.get).toHaveBeenCalledWith('/users', {
                params: { pageNumber: 0, pageSize: 10, order: SortOrder.ASC },
            });
            expect(result).toEqual(mockPageResponse);
        });

        it('should forward custom pagination and sort params', async () => {
            vi.mocked(api.get).mockResolvedValue({ data: mockPageResponse });

            await userService.getAllUsers(2, 25, SortOrder.DESC);

            expect(api.get).toHaveBeenCalledWith('/users', {
                params: { pageNumber: 2, pageSize: 25, order: SortOrder.DESC },
            });
        });
    });

    describe('createUser', () => {
        it('should post user creation request and return created user', async () => {
            const request: UserCreationRequest = {
                username: 'newuser',
                email: 'new@example.com',
                role: UserRole.USER,
                password: 'Passw0rd!',
            };
            vi.mocked(api.post).mockResolvedValue({ data: mockUser });

            const result = await userService.createUser(request);

            expect(api.post).toHaveBeenCalledWith('/users', request);
            expect(result).toEqual(mockUser);
        });
    });

    describe('updateMyself', () => {
        it('should put to /users/me and return updated user', async () => {
            const request = { username: 'updated', email: null };
            vi.mocked(api.put).mockResolvedValue({ data: { ...mockUser, username: 'updated' } });

            const result = await userService.updateMyself(request);

            expect(api.put).toHaveBeenCalledWith('/users/me', request);
            expect(result.username).toBe('updated');
        });
    });

    describe('updateUserById', () => {
        it('should put to /users/:id and return updated user', async () => {
            const request: AdminUserUpdateRequest = {
                username: 'admin-updated',
                email: 'admin@example.com',
                role: UserRole.ADMIN,
            };
            vi.mocked(api.put).mockResolvedValue({ data: { ...mockUser, ...request } });

            const result = await userService.updateUserById('user-1', request);

            expect(api.put).toHaveBeenCalledWith('/users/user-1', request);
            expect(result.role).toBe(UserRole.ADMIN);
        });
    });

    describe('updateMyPassword', () => {
        it('should patch /users/me/password', async () => {
            const request: PasswordUpdateRequest = {
                password: 'OldPass1!',
                newPassword: 'NewPass1!',
            };
            vi.mocked(api.patch).mockResolvedValue({});

            await userService.updateMyPassword(request);

            expect(api.patch).toHaveBeenCalledWith('/users/me/password', request);
        });
    });

    describe('deleteUser', () => {
        it('should delete /users/:id', async () => {
            vi.mocked(api.delete).mockResolvedValue({});

            await userService.deleteUser('user-1');

            expect(api.delete).toHaveBeenCalledWith('/users/user-1');
        });
    });
});
