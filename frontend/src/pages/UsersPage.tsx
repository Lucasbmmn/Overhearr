import { ArrowDownWideNarrow, ArrowUpNarrowWide, Plus } from "lucide-react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useSearchParams } from "react-router-dom";
import ConfirmDialog from "../components/ui/ConfirmDialog";
import CreateUserModal from "../components/users/CreateUserModal";
import EditUserModal from "../components/users/EditUserModal";
import UserTable from "../components/users/UserTable";
import { useUsers } from "../hooks/useUsers";
import { userService } from "../services/userService";
import { SortOrder } from "../types/common";
import type { User } from "../types/user";

const UsersPage = () => {
    const { t } = useTranslation(['common', 'users']);
    const [searchParams, setSearchParams] = useSearchParams();

    const page = parseInt(searchParams.get("page") || "0", 10);
    const size = parseInt(searchParams.get("size") || "10", 10);
    const sortOrder = searchParams.get("order") === SortOrder.DESC ? SortOrder.DESC : SortOrder.ASC;

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [editModal, setEditModal] = useState<{ isOpen: boolean; user?: User }>({
        isOpen: false,
    });

    const [deleteModal, setDeleteModal] = useState<{
        isOpen: boolean;
        userId?: string;
    }>({
        isOpen: false,
    });

    const { usersPage, refetch } = useUsers(page, size, sortOrder);

    const updateParams = (updates: Partial<{ page: number; size: number; order: SortOrder }>) => {
        setSearchParams(prev => {
            const newParams = new URLSearchParams(prev);
            if (updates.page !== undefined) newParams.set("page", updates.page.toString());
            if (updates.size !== undefined) newParams.set("size", updates.size.toString());
            if (updates.order !== undefined) newParams.set("order", updates.order);
            return newParams;
        });
    };

    const handleDeleteUser = async () => {
        if (deleteModal.userId) {
            try {
                await userService.deleteUser(deleteModal.userId);
                setDeleteModal({ isOpen: false, userId: undefined });
                refetch();
            } catch (error) {
                console.error("Failed to delete user", error);
            }
        }
    };

    const toggleSort = () => {
        const newOrder = sortOrder === SortOrder.ASC ? SortOrder.DESC : SortOrder.ASC;
        updateParams({ order: newOrder });
    };

    const handleNextPage = () => {
        if (usersPage && page < usersPage.meta.totalPages - 1) {
            updateParams({ page: page + 1 });
        }
    };

    const handlePreviousPage = () => {
        updateParams({ page: Math.max(0, page - 1) });
    };

    const handleSetPageSize = (newSize: number) => {
        updateParams({ size: newSize, page: 0 })
    };

    return (
        <div className="animate-fade-in p-6 w-full max-w-7xl mx-auto space-y-6">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h1 className="text-3xl font-bold text-gray-100 tracking-tight">
                        {t('users:title')}
                    </h1>
                </div>

                <div className="flex items-center gap-3">
                    <button
                        onClick={toggleSort}
                        className="flex items-center gap-2 px-4 py-2 bg-gray-800 border border-gray-700 text-gray-300 rounded-lg hover:bg-gray-700 hover:text-white transition-all"
                    >
                        {sortOrder === SortOrder.ASC ? (
                            <ArrowDownWideNarrow className="w-4 h-4" />
                        ) : (
                            <ArrowUpNarrowWide className="w-4 h-4" />
                        )}
                    </button>

                    <button
                        onClick={() => setIsCreateModalOpen(true)}
                        className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg font-medium shadow-lg shadow-indigo-900/20 transition-all active:scale-95"
                    >
                        <Plus className="w-4 h-4" />
                        <span>{t('users:create_user')}</span>
                    </button>
                </div>
            </div>

            <CreateUserModal
                isOpen={isCreateModalOpen}
                onClose={() => setIsCreateModalOpen(false)}
                onSuccess={() => {
                    setIsCreateModalOpen(false);
                    refetch();
                }}
            />

            <EditUserModal
                isOpen={editModal.isOpen}
                user={editModal.user}
                onClose={() => setEditModal({ isOpen: false, user: undefined })}
                onSuccess={() => {
                    setEditModal({ isOpen: false, user: undefined });
                    refetch();
                }}
            />

            <ConfirmDialog
                isOpen={deleteModal.isOpen}
                onClose={() => setDeleteModal({ isOpen: false, userId: undefined })}
                onConfirm={handleDeleteUser}
                title={t('common.confirm')}
                message={t('users:delete_confirm')}
            />

            <div className="bg-gray-800/50 rounded-lg overflow-hidden">
                <UserTable
                    userPage={usersPage}
                    pageSize={size}
                    editUser={(user) => setEditModal({ isOpen: true, user })}
                    deleteUser={(id) => setDeleteModal({ isOpen: true, userId: id })}
                    setPageSize={handleSetPageSize}
                    previousPage={handlePreviousPage}
                    nextPage={handleNextPage}
                />
            </div>
        </div>
    )
}

export default UsersPage;