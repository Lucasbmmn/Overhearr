import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import CreateUserModal from './CreateUserModal';
import { userService } from '../../services/userService';
import { UserRole } from '../../types/user';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key: string) => key,
        i18n: { changeLanguage: vi.fn(), language: 'en' },
    }),
}));

vi.mock('../../services/userService', () => ({
    userService: {
        createUser: vi.fn(),
    },
}));

// Mock zod schemas to avoid i18n dependency in validation messages
vi.mock('../../schemas/userSchemas', async () => {
    const { z } = await import('zod');

    const createUserSchema = z.object({
        username: z.string().min(3, 'Username too short'),
        email: z.email('Invalid email').optional().or(z.literal('')),
        role: z.enum(['USER', 'ADMIN']),
        password: z.string().min(8, 'Password too short'),
    });

    return {
        useUserSchemas: () => ({
            createUserSchema,
            editUserSchema: z.object({}),
            passwordRulesSchema: z.string(),
        }),
    };
});

describe('CreateUserModal', () => {
    const defaultProps = {
        isOpen: true,
        onClose: vi.fn(),
        onSuccess: vi.fn(),
    };

    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('returns null when isOpen is false', () => {
        const { container } = render(
            <CreateUserModal {...defaultProps} isOpen={false} />
        );

        expect(container.innerHTML).toBe('');
    });

    it('renders form fields when open', () => {
        render(<CreateUserModal {...defaultProps} />);

        expect(screen.getByText('users:edit.username')).toBeInTheDocument();
        expect(screen.getByText('users:edit.email')).toBeInTheDocument();
        expect(screen.getByText('users:user_table.role')).toBeInTheDocument();
        expect(screen.getByText('users:password')).toBeInTheDocument();
    });

    it('renders create and cancel buttons', () => {
        render(<CreateUserModal {...defaultProps} />);

        expect(screen.getByText('common.create')).toBeInTheDocument();
        expect(screen.getByText('common.cancel')).toBeInTheDocument();
    });

    it('calls onClose when cancel is clicked', () => {
        const onClose = vi.fn();
        render(<CreateUserModal {...defaultProps} onClose={onClose} />);

        fireEvent.click(screen.getByText('common.cancel'));
        expect(onClose).toHaveBeenCalled();
    });

    it('submits form with valid data and calls onSuccess', async () => {
        const onSuccess = vi.fn();
        const createdUser = {
            id: 'new-id',
            username: 'newuser',
            email: 'new@example.com',
            role: UserRole.USER,
            createdAt: '2026-01-01T00:00:00Z',
            updatedAt: '2026-01-01T00:00:00Z',
        };
        vi.mocked(userService.createUser).mockResolvedValue(createdUser);

        render(<CreateUserModal {...defaultProps} onSuccess={onSuccess} />);

        fireEvent.change(screen.getByPlaceholderText('jdoe'), {
            target: { value: 'newuser' },
        });
        fireEvent.change(screen.getByPlaceholderText('john@example.com'), {
            target: { value: 'new@example.com' },
        });
        fireEvent.change(screen.getByPlaceholderText('••••••••'), {
            target: { value: 'StrongPass1!' },
        });

        fireEvent.click(screen.getByText('common.create'));

        await waitFor(() => {
            expect(userService.createUser).toHaveBeenCalledWith({
                username: 'newuser',
                email: 'new@example.com',
                role: UserRole.USER,
                password: 'StrongPass1!',
            });
        });

        await waitFor(() => {
            expect(onSuccess).toHaveBeenCalled();
        });
    });

    it('shows root error when API call fails', async () => {
        vi.mocked(userService.createUser).mockRejectedValue(new Error('Server error'));

        render(<CreateUserModal {...defaultProps} />);

        fireEvent.change(screen.getByPlaceholderText('jdoe'), {
            target: { value: 'newuser' },
        });
        fireEvent.change(screen.getByPlaceholderText('••••••••'), {
            target: { value: 'StrongPass1!' },
        });

        fireEvent.click(screen.getByText('common.create'));

        await waitFor(() => {
            expect(screen.getByText('app.internal_server_error')).toBeInTheDocument();
        });
    });
});
