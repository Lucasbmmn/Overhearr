import api from "../lib/axios";
import { type PageResponse, SortOrder } from "../types/common";
import type {
    AdminUserUpdateRequest,
    IUserService,
    PasswordUpdateRequest,
    User,
    UserCreationRequest,
    UserUpdateRequest
} from "../types/user";

export const userService: IUserService = {
    async getAllUsers(pageNumber = 0, pageSize = 10, order = SortOrder.ASC): Promise<PageResponse<User>> {
        const response = await api.get<PageResponse<User>>('/users', {
            params: { pageNumber, pageSize, order },
        })
        return response.data;
    },

    async createUser(request: UserCreationRequest): Promise<User> {
        const response = await api.post<User>('/users', request)
        return response.data;
    },

    async updateMyself(request: UserUpdateRequest): Promise<User> {
        const response = await api.put<User>('/users/me', request);
        return response.data;
    },

    async updateUserById(id: string, request: AdminUserUpdateRequest): Promise<User> {
        const response = await api.put<User>(`/users/${id}`, request);
        return response.data;
    },

    async updateMyPassword(request: PasswordUpdateRequest) {
        await api.patch('/users/me/password', request);
    },

    async deleteUser(id: string) {
        await api.delete(`/users/${id}`);
    }
};