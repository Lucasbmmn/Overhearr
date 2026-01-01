import type { User } from './user';

export interface LoginRequest {
    identifier: string;
    password: string;
}

export interface AuthResponse {
    accessToken: string;
    user: User;
}

export interface IAuthService {
    login(credentials: LoginRequest): Promise<AuthResponse>;
    logout(): Promise<void>;
}