import api from '../lib/axios';
import type { AuthResponse, LoginRequest } from '../types/auth';

export const authService = {
    async login(credentials: LoginRequest): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/login', credentials);
        return response.data;
    },

    async logout(): Promise<void> {
        await api.post<AuthResponse>('/auth/logout');
    }
};