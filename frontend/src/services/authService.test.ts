import { describe, it, expect, vi, beforeEach } from 'vitest';
import { authService } from './authService';
import api from '../lib/axios';

vi.mock('../lib/axios', () => ({
    default: {
        post: vi.fn(),
    },
}));

describe('authService', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    describe('login', () => {
        it('should call api.post with correct arguments and return data', async () => {
            const mockCredentials = { identifier: 'testUser', password: 'password123' };
            const mockAuthResponse = {
                accessToken: 'token',
                user: {
                    id: 'userId',
                    username: 'testUser',
                    email: 'test@example.com',
                    role: 'USER',
                    createdAt: '2026-01-01T14:45:06.167Z',
                    updatedAt: '2026-01-01T14:45:06.167Z'
                }
            };

            vi.mocked(api.post).mockResolvedValue({ data: mockAuthResponse });

            const result = await authService.login(mockCredentials);

            expect(api.post).toHaveBeenCalledTimes(1);
            expect(api.post).toHaveBeenCalledWith('/auth/login', mockCredentials);

            expect(result).toEqual(mockAuthResponse);
        });
    });

    describe('logout', () => {
        it('should call api.post to logout endpoint', async () => {
            vi.mocked(api.post).mockResolvedValue({ data: {} });

            await authService.logout();

            expect(api.post).toHaveBeenCalledTimes(1);
            expect(api.post).toHaveBeenCalledWith('/auth/logout');
        });
    });
});