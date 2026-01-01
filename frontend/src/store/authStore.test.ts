import { act } from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { AuthResponse, LoginRequest } from "../types/auth";
import { type User, UserRole } from "../types/user";
import { createAuthStore } from "./authStore";

const mockAuthService = {
    login: vi.fn(),
    logout: vi.fn(),
}

describe('authStore', () => {
    let useStore: ReturnType<typeof createAuthStore>;

    beforeEach(() => {
        vi.clearAllMocks();
        localStorage.clear();
        useStore = createAuthStore(mockAuthService);
    });

    it('should have initial state unauthenticated', () => {
        const state = useStore.getState();
        expect(state.user).toBeNull();
        expect(state.isAuthenticated).toBe(false);
        expect(state.isLoading).toBe(false);
        expect(state.errorKey).toBeNull();
    });

    it('should handle successful login', async () => {
        const mockUser: User = { id: 'userId', username: 'testuser', email: 'test@test.com', role: UserRole.USER,
            createdAt: '2026-01-01T14:45:06.167Z', updatedAt: '2026-01-01T14:45:06.167Z' };
        const mockResponse: AuthResponse = { accessToken: 'accessToken', user: mockUser };

        mockAuthService.login.mockResolvedValue(mockResponse);

        const credentials: LoginRequest = { identifier: "testuser", password: "password" };

        await act(async () => {
            await useStore.getState().login(credentials);
        });

        const state = useStore.getState();
        expect(state.user).toBe(mockUser);
        expect(state.isAuthenticated).toBe(true);
        expect(state.isLoading).toBe(false);
        expect(state.errorKey).toBeNull();
    });

    it('should handle login failure', async () => {
        const mockError = {
            isAxiosError: true,
            response: { status: 401 }
        };
        mockAuthService.login.mockRejectedValue(mockError);

        const credentials = { identifier: 'wrong', password: 'wrong' };

        try {
            await act(async () => {
                await useStore.getState().login(credentials);
            });
        } catch {
            // State is verified bellow
        }

        const state = useStore.getState();
        expect(state.user).toBeNull();
        expect(state.isAuthenticated).toBe(false);
        expect(state.isLoading).toBe(false);
        expect(state.errorKey).toBe('auth.credential_error');
    });
})