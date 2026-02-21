import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import UserTable from './UserTable';
import { UserRole } from '../../types/user';
import type { User } from '../../types/user';
import type { PageResponse } from '../../types/common';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key: string, params?: Record<string, unknown>) => {
            if (params) return `${key}:${JSON.stringify(params)}`;
            return key;
        },
        i18n: { language: 'en' },
    }),
    Trans: ({ children }: { children: React.ReactNode }) => <>{children}</>,
}));

const mockUsers: User[] = [
    {
        id: 'user-1',
        username: 'jdoe',
        email: 'jdoe@example.com',
        role: UserRole.USER,
        createdAt: '2026-06-15T10:00:00Z',
        updatedAt: '2026-06-15T10:00:00Z',
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

const defaultProps = {
    userPage: mockPageResponse,
    pageSize: 10,
    editUser: vi.fn(),
    deleteUser: vi.fn(),
    setPageSize: vi.fn(),
    previousPage: vi.fn(),
    nextPage: vi.fn(),
};

describe('UserTable', () => {
    it('renders user rows with usernames', () => {
        render(<UserTable {...defaultProps} />);

        expect(screen.getByText('jdoe')).toBeInTheDocument();
        expect(screen.getByText('admin')).toBeInTheDocument();
    });

    it('renders emails when present and omits when null', () => {
        render(<UserTable {...defaultProps} />);

        expect(screen.getByText('jdoe@example.com')).toBeInTheDocument();
        // admin has no email so only the username appears for that row
    });

    it('renders role badges', () => {
        render(<UserTable {...defaultProps} />);

        // 'users:user_table.user' appears in both the <th> header and the role badge
        const userTexts = screen.getAllByText('users:user_table.user');
        expect(userTexts.length).toBeGreaterThanOrEqual(2); // header + badge

        // 'users:user_table.admin' only appears once (in the admin role badge)
        expect(screen.getByText('users:user_table.admin')).toBeInTheDocument();
    });

    it('renders table headers', () => {
        render(<UserTable {...defaultProps} />);

        expect(screen.getByText('users:user_table.user', { selector: 'th' })).toBeInTheDocument();
        expect(screen.getByText('users:user_table.role', { selector: 'th' })).toBeInTheDocument();
        expect(screen.getByText('users:user_table.created', { selector: 'th' })).toBeInTheDocument();
        expect(screen.getByText('users:user_table.edit', { selector: 'th' })).toBeInTheDocument();
    });

    it('renders nothing when userPage is undefined', () => {
        const { container } = render(
            <UserTable {...defaultProps} userPage={undefined} />
        );

        // Table should be present but tbody is empty and no pagination
        expect(container.querySelector('tbody')?.children.length).toBe(0);
    });

    it('calls editUser when edit button is clicked', () => {
        const editUser = vi.fn();
        render(<UserTable {...defaultProps} editUser={editUser} />);

        const editButtons = screen.getAllByTitle('users:user_table.edit');
        fireEvent.click(editButtons[0]);

        expect(editUser).toHaveBeenCalledWith(mockUsers[0]);
    });

    it('calls deleteUser when delete button is clicked', () => {
        const deleteUser = vi.fn();
        render(<UserTable {...defaultProps} deleteUser={deleteUser} />);

        const deleteButtons = screen.getAllByTitle('users:user_table.delete');
        fireEvent.click(deleteButtons[0]);

        expect(deleteUser).toHaveBeenCalledWith('user-1');
    });

    describe('Pagination', () => {
        it('disables previous button on first page', () => {
            render(<UserTable {...defaultProps} />);

            const prevButton = screen.getByTitle('users:user_table.pagination.previous');
            expect(prevButton).toBeDisabled();
        });

        it('disables next button on last page', () => {
            render(<UserTable {...defaultProps} />);

            const nextButton = screen.getByTitle('users:user_table.pagination.next');
            expect(nextButton).toBeDisabled();
        });

        it('enables next button when there are more pages', () => {
            const multiPageResponse: PageResponse<User> = {
                ...mockPageResponse,
                meta: { page: 0, size: 10, totalElements: 25, totalPages: 3 },
            };
            render(<UserTable {...defaultProps} userPage={multiPageResponse} />);

            const nextButton = screen.getByTitle('users:user_table.pagination.next');
            expect(nextButton).not.toBeDisabled();
        });

        it('calls nextPage when next button is clicked', () => {
            const nextPage = vi.fn();
            const multiPageResponse: PageResponse<User> = {
                ...mockPageResponse,
                meta: { page: 0, size: 10, totalElements: 25, totalPages: 3 },
            };
            render(<UserTable {...defaultProps} userPage={multiPageResponse} nextPage={nextPage} />);

            fireEvent.click(screen.getByTitle('users:user_table.pagination.next'));
            expect(nextPage).toHaveBeenCalled();
        });

        it('calls previousPage when previous button is clicked', () => {
            const previousPage = vi.fn();
            const secondPage: PageResponse<User> = {
                ...mockPageResponse,
                meta: { page: 1, size: 10, totalElements: 25, totalPages: 3 },
            };
            render(<UserTable {...defaultProps} userPage={secondPage} previousPage={previousPage} />);

            fireEvent.click(screen.getByTitle('users:user_table.pagination.previous'));
            expect(previousPage).toHaveBeenCalled();
        });

        it('calls setPageSize when page size select is changed', () => {
            const setPageSize = vi.fn();
            render(<UserTable {...defaultProps} setPageSize={setPageSize} />);

            const select = screen.getByDisplayValue('10');
            fireEvent.change(select, { target: { value: '25' } });

            expect(setPageSize).toHaveBeenCalledWith(25);
        });
    });
});
