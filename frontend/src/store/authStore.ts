import axios from 'axios';
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { authService } from '../services/authService';
import type { LoginRequest, IAuthService } from '../types/auth';
import type { User } from '../types/user';

interface AuthState {
    user: User | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    errorKey: string | null;

    login: (credentials: LoginRequest) => Promise<void>;
    logout: () => void;
    clearError: () => void;
}

export const createAuthStore = (authService: IAuthService) => create<AuthState>()(
    persist(
        (set) => ({
            user: null,
            isAuthenticated: false,
            isLoading: false,
            errorKey: null,

            login: async (credentials) => {
                set({ isLoading: true, errorKey: null });
                try {
                    const data = await authService.login(credentials);
                    set({
                        user: data.user,
                        isAuthenticated: true,
                        isLoading: false
                    });
                } catch (err) {
                    let key = 'app.internal_server_error';
                    console.log(err);

                    if (axios.isAxiosError(err)) {
                        if (err.response) {
                            const status = err.response.status;
                            if (status === 401) {
                                key = 'auth.credential_error';
                            } else if (status >= 500) {
                                key = 'app.internal_server_error';
                            }
                        } else if (err.request) {
                            key = 'app.network_error';
                        }
                    } else {
                        console.error('An unexpected error occurred:', err);
                    }

                    set({
                        errorKey: key,
                        isLoading: false,
                        isAuthenticated: false
                    });
                    throw err;
                }
            },

            logout: () => {
                authService.logout().catch(console.error);
                set({ user: null, isAuthenticated: false });
            },

            clearError: () => set({ errorKey: null }),
        }),
        {
            name: 'overhearr_authenticated',
            partialize: (state) => ({
                user: state.user,
                isAuthenticated: state.isAuthenticated
            }),
        }
    )
);

export const useAuthStore = createAuthStore(authService);