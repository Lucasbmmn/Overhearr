import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import UsersPage from './UsersPage';
import { userService } from '../services/userService';
import { UserRole } from '../types/user';
import type { User } from '../types/user';
import type { PageResponse } from '../types/common';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key: string, params?: Record<string, unknown>) => {
            if (params) return `${key}:${JSON.stringify(params)}`;
            return key;
        },
        i18n: { changeLanguage: vi.fn(), language: 'en' },
    }),
    Trans: ({ children }: { children: React.ReactNode }) => <>{children}</>,
}));

const mockRefetch = vi.fn();

vi.mock('../hooks/useUsers', () => ({
    useUsers: () => ({
        usersPage: mockPageResponse,
        error: null,
        refetch: mockRefetch,
    }),
}));

vi.mock('../services/userService', () => ({
    userService: {
        createUser: vi.fn(),
        updateUserById: vi.fn(),
        deleteUser: vi.fn(),
    },
}));

// Mock schemas for modals
vi.mock('../schemas/userSchemas', async () => {
    const { z } = await import('zod');

    const baseSchema = z.object({
        username: z.string().min(3, 'Username too short'),
        email: z.email('Invalid email').optional().or(z.literal('')),
        role: z.enum(['USER', 'ADMIN']),
    });

    return {
        useUserSchemas: () => ({
            createUserSchema: baseSchema.extend({
                password: z.string().min(8, 'Password too short'),
            }),
            editUserSchema: baseSchema,
            passwordRulesSchema: z.string(),
        }),
    };
});

const mockUsers: User[] = [
    {
        id: 'user-1',
        username: 'jdoe',
        email: 'jdoe@example.com',
        role: UserRole.USER,
        createdAt: '2026-01-01T00:00:00Z',
        updatedAt: '2026-01-01T00:00:00Z',
    },
    {
        id: 'user-2',
        username: 'admin',
        email: null,
        role: UserRole.ADMIN,
        createdAt: '2026-01-01T00:00:00Z',
        updatedAt: '2026-01-01T00:00:00Z',
    },
];

const mockPageResponse: PageResponse<User> = {
    data: mockUsers,
    meta: { page: 0, size: 10, totalElements: 2, totalPages: 1 },
};

const renderPage = () =>
    render(
        <MemoryRouter>
            <UsersPage />
        </MemoryRouter>
    );

describe('UsersPage', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('renders page title and create button', () => {
        renderPage();

        expect(screen.getByText('users:title')).toBeInTheDocument();
        expect(screen.getByText('users:create_user')).toBeInTheDocument();
    });

    it('renders user data in the table', () => {
        renderPage();

        expect(screen.getByText('jdoe')).toBeInTheDocument();
        expect(screen.getByText('admin')).toBeInTheDocument();
    });

    it('opens CreateUserModal when create button is clicked', () => {
        renderPage();

        fireEvent.click(screen.getByText('users:create_user'));

        // Modal title for create user
        expect(screen.getByText('users:user_table.create_user')).toBeInTheDocument();
    });

    it('opens EditUserModal when edit button is clicked', () => {
        renderPage();

        const editButtons = screen.getAllByTitle('users:user_table.edit');
        fireEvent.click(editButtons[0]);

        // Modal title for edit user
        expect(screen.getByText('users:user_table.edit_user')).toBeInTheDocument();
        // Pre-filled form
        expect(screen.getByDisplayValue('jdoe')).toBeInTheDocument();
    });

    it('opens ConfirmDialog when delete button is clicked', () => {
        renderPage();

        const deleteButtons = screen.getAllByTitle('users:user_table.delete');
        fireEvent.click(deleteButtons[0]);

        expect(screen.getByText('users:delete_confirm')).toBeInTheDocument();
    });

    it('calls deleteUser and refetch on confirm', async () => {
        vi.mocked(userService.deleteUser).mockResolvedValue(undefined);

        renderPage();

        // Open confirm dialog
        const deleteButtons = screen.getAllByTitle('users:user_table.delete');
        fireEvent.click(deleteButtons[0]);

        // Click delete confirm button
        fireEvent.click(screen.getByText('common.delete'));

        await waitFor(() => {
            expect(userService.deleteUser).toHaveBeenCalledWith('user-1');
        });

        await waitFor(() => {
            expect(mockRefetch).toHaveBeenCalled();
        });
    });
});
