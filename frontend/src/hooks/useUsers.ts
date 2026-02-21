import { useState, useEffect, useCallback } from 'react';
import { userService } from '../services/userService';
import { type PageResponse, SortOrder } from '../types/common';
import type { User } from '../types/user';

export const useUsers = (page: number, size: number, sortOrder: SortOrder) => {
    const [usersPage, setUsersPage] = useState<PageResponse<User>>();
    const [error, setError] = useState<unknown>(null);

    const fetchUsers = useCallback(async () => {
        try {
            const data = await userService.getAllUsers(page, size, sortOrder);
            setUsersPage(data);
            setError(null);
        } catch (err) {
            setError(err);
        }
    }, [page, size, sortOrder]);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        fetchUsers();
    }, [fetchUsers]);

    return { usersPage, error, refetch: fetchUsers };
};