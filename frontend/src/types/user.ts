import type { PageResponse } from './common';

export enum UserRole {
    USER = 'USER',
    ADMIN = 'ADMIN',
}

export interface User {
    id: string;
    username: string;
    email: string | null;
    role: UserRole;
    createdAt: string;
    updatedAt: string;
}

export interface UserCreationRequest {
    username: string;
    email: string | null;
    role: UserRole;
    password: string;
}

export interface PasswordUpdateRequest {
    password: string;
    newPassword: string;
}

export interface UserUpdateRequest {
    username: string;
    email: string | null;
}

export interface AdminUserUpdateRequest {
    username: string;
    email: string | null;
    role: UserRole;
}

export interface IUserService {
    getAllUsers(pageNumber?: number, pageSize?: number, order?: string): Promise<PageResponse<User>>;
    createUser(request: UserCreationRequest): Promise<User>;
    updateMyself(request: UserUpdateRequest): Promise<User>;
    updateUserById(id: string, request: AdminUserUpdateRequest): Promise<User>;
    updateMyPassword(request: PasswordUpdateRequest): Promise<void>;
    deleteUser(id: string): Promise<void>;
}