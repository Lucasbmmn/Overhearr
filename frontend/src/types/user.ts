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