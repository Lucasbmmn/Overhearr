import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import EditUserModal from './EditUserModal';
import { userService } from '../../services/userService';
import { UserRole } from '../../types/user';
import type { User } from '../../types/user';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key: string) => key,
        i18n: { changeLanguage: vi.fn(), language: 'en' },
    }),
}));

vi.mock('../../services/userService', () => ({
    userService: {
        updateUserById: vi.fn(),
    },
}));

vi.mock('../../schemas/userSchemas', async () => {
    const { z } = await import('zod');

    const editUserSchema = z.object({
        username: z.string().min(3, 'Username too short'),
        email: z.email('Invalid email').optional().or(z.literal('')),
        role: z.enum(['USER', 'ADMIN']),
    });

    return {
        useUserSchemas: () => ({
            createUserSchema: z.object({}),
            editUserSchema,
            passwordRulesSchema: z.string(),
        }),
    };
});

const mockUser: User = {
    id: 'user-1',
    username: 'jdoe',
    email: 'jdoe@example.com',
    role: UserRole.USER,
    createdAt: '2026-01-01T00:00:00Z',
    updatedAt: '2026-01-01T00:00:00Z',
};

describe('EditUserModal', () => {
    const defaultProps = {
        isOpen: true,
        user: mockUser,
        onClose: vi.fn(),
        onSuccess: vi.fn(),
    };

    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('returns null when isOpen is false', () => {
        const { container } = render(
            <EditUserModal {...defaultProps} isOpen={false} />
        );

        expect(container.innerHTML).toBe('');
    });

    it('returns null when user is undefined', () => {
        const { container } = render(
            <EditUserModal {...defaultProps} user={undefined} />
        );

        expect(container.innerHTML).toBe('');
    });

    it('pre-fills form with user data', () => {
        render(<EditUserModal {...defaultProps} />);

        const usernameInput = screen.getByDisplayValue('jdoe') as HTMLInputElement;
        expect(usernameInput).toBeInTheDocument();

        const emailInput = screen.getByDisplayValue('jdoe@example.com') as HTMLInputElement;
        expect(emailInput).toBeInTheDocument();
    });

    it('renders save and cancel buttons', () => {
        render(<EditUserModal {...defaultProps} />);

        expect(screen.getByText('common.save')).toBeInTheDocument();
        expect(screen.getByText('common.cancel')).toBeInTheDocument();
    });

    it('calls onClose when cancel is clicked', () => {
        const onClose = vi.fn();
        render(<EditUserModal {...defaultProps} onClose={onClose} />);

        fireEvent.click(screen.getByText('common.cancel'));
        expect(onClose).toHaveBeenCalled();
    });

    it('submits updated data and calls onSuccess', async () => {
        const onSuccess = vi.fn();
        vi.mocked(userService.updateUserById).mockResolvedValue({
            ...mockUser,
            username: 'updated',
        });

        render(<EditUserModal {...defaultProps} onSuccess={onSuccess} />);

        const usernameInput = screen.getByDisplayValue('jdoe');
        fireEvent.change(usernameInput, { target: { value: 'updated' } });

        fireEvent.click(screen.getByText('common.save'));

        await waitFor(() => {
            expect(userService.updateUserById).toHaveBeenCalledWith('user-1', {
                username: 'updated',
                email: 'jdoe@example.com',
                role: UserRole.USER,
            });
        });

        await waitFor(() => {
            expect(onSuccess).toHaveBeenCalled();
        });
    });

    it('shows root error when API call fails', async () => {
        vi.mocked(userService.updateUserById).mockRejectedValue(new Error('Server error'));

        render(<EditUserModal {...defaultProps} />);

        fireEvent.click(screen.getByText('common.save'));

        await waitFor(() => {
            expect(screen.getByText('app.internal_server_error')).toBeInTheDocument();
        });
    });
});
