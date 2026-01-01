import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

const mockUseAuthStore = vi.fn();
vi.mock('../store/authStore', () => ({
    useAuthStore: () => mockUseAuthStore(),
}));

describe('ProtectedRoute', () => {
    it('should redirect to login if not authenticated', () => {
        mockUseAuthStore.mockReturnValue({ isAuthenticated: false });

        render(
            <MemoryRouter initialEntries={['/protected']}>
                <Routes>
                    <Route path="/login" element={<div>Login Page</div>} />
                    <Route element={<ProtectedRoute />}>
                        <Route path="/protected" element={<div>Secret Content</div>} />
                    </Route>
                </Routes>
            </MemoryRouter>
        );

        expect(screen.getByText('Login Page')).toBeInTheDocument();
        expect(screen.queryByText('Secret Content')).not.toBeInTheDocument();
    });

    it('should render child route if authenticated', () => {
        mockUseAuthStore.mockReturnValue({ isAuthenticated: true });

        render(
            <MemoryRouter initialEntries={['/protected']}>
                <Routes>
                    <Route path="/login" element={<div>Login Page</div>} />
                    <Route element={<ProtectedRoute />}>
                        <Route path="/protected" element={<div>Secret Content</div>} />
                    </Route>
                </Routes>
            </MemoryRouter>
        );

        expect(screen.getByText('Secret Content')).toBeInTheDocument();
        expect(screen.queryByText('Login Page')).not.toBeInTheDocument();
    });
});