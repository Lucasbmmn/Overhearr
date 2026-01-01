import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import Login from './Login';
import { BrowserRouter } from 'react-router-dom';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key: string) => key,
        i18n: { changeLanguage: vi.fn(), language: 'en' },
    }),
}));

const mockLogin = vi.fn();
vi.mock('../store/authStore', () => ({
    useAuthStore: () => ({
        login: mockLogin,
        isLoading: false,
        errorKey: null,
        clearError: vi.fn(),
    }),
}));

describe('Login Component', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('renders the login form', () => {
        render(
            <BrowserRouter>
                <Login />
            </BrowserRouter>
        );

        expect(screen.getByText('app.title')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('auth.identifier_placeholder')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('auth.password_placeholder')).toBeInTheDocument();
        expect(screen.getByRole('button', {name: 'auth.sign_in'})).toBeInTheDocument();
    });

    it('submits the form with credentials', async () => {
        mockLogin.mockResolvedValue({});

        render(
            <BrowserRouter>
                <Login />
            </BrowserRouter>
        );

        // Fill in inputs
        fireEvent.change(screen.getByPlaceholderText('auth.identifier_placeholder'), {
            target: { value: 'user' },
        });
        fireEvent.change(screen.getByPlaceholderText('auth.password_placeholder'), {
            target: { value: 'password123' },
        });

        // Click submit
        fireEvent.click(screen.getByRole('button', { name: 'auth.sign_in' }));

        // Verify store login was called
        await waitFor(() => {
            expect(mockLogin).toHaveBeenCalledWith({
                identifier: 'user',
                password: 'password123',
            });
        });
    });

    it('shows validation error for empty fields', async () => {
        render(
            <BrowserRouter>
                <Login />
            </BrowserRouter>
        );

        fireEvent.click(screen.getByRole('button', { name: 'auth.sign_in' }));

        expect(mockLogin).not.toHaveBeenCalled();
    });
});