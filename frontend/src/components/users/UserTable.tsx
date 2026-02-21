import { ChevronLeft, ChevronRight, Edit2, Shield, Trash2, UserIcon } from 'lucide-react';
import { Trans, useTranslation } from 'react-i18next';
import type { PageResponse } from '../../types/common';
import type { User } from '../../types/user';


interface UserRowProps {
    user: User;
    locale: string;
    onEdit: (user: User) => void;
    onDelete: (id: string) => void;
}

const UserRow = ({ user, locale, onEdit, onDelete }: UserRowProps) => {
    const { t } = useTranslation(['common', 'users']);

    return (
        <tr className="group hover:bg-gray-700/30 transition-colors duration-200">
            <td className="px-6 py-4 whitespace-nowrap">
                <div className="flex items-center">
                    <div className="shrink-0 h-10 w-10">
                        <img
                            className="h-10 w-10 rounded-full ring-2 ring-gray-700 object-cover bg-gray-600"
                            src={`https://api.dicebear.com/9.x/shapes/svg?seed=${user.id}`}
                            alt=""
                        />
                    </div>
                    <div className="ml-4">
                        <div className="text-sm font-medium text-gray-100 group-hover:text-white transition-colors">
                            {user.username}
                        </div>
                        {user.email && (
                            <div className="text-sm text-gray-500 group-hover:text-gray-400 transition-colors">
                                {user.email}
                            </div>
                        )}
                    </div>
                </div>
            </td>
            <td className="px-6 py-4 whitespace-nowrap">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${user.role === 'ADMIN'
                    ? 'bg-indigo-900/50 text-indigo-200 border-indigo-700/50'
                    : 'bg-gray-700/50 text-gray-300 border-gray-600/50'
                    }`}>
                    {user.role === 'ADMIN' ? <Shield className="w-3 h-3 mr-1" /> : <UserIcon className="w-3 h-3 mr-1" />}
                    {t(`users:user_table.${user.role.toLowerCase()}`)}
                </span>
            </td>
            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-400">
                {new Date(user.createdAt).toLocaleDateString(locale, {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric'
                })}
            </td>
            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button
                    onClick={() => onEdit(user)}
                    className="text-gray-500 hover:text-yellow-400 transition-colors p-2 rounded-lg hover:bg-yellow-900/20"
                    title={t('users:user_table.edit')}
                >
                    <Edit2 className="w-6 h-6" />
                </button>
                <button
                    onClick={() => onDelete(user.id)}
                    className="text-gray-500 hover:text-red-400 transition-colors p-2 rounded-lg hover:bg-red-900/20"
                    title={t('users:user_table.delete')}
                >
                    <Trash2 className="w-6 h-6" />
                </button>
            </td>
        </tr>
    );
};

interface PaginationProps {
    meta: PageResponse<User>['meta'];
    pageSize: number;
    onPageSizeChange: (size: number) => void;
    onPrevious: () => void;
    onNext: () => void;
}

const Pagination = ({ meta, pageSize, onPageSizeChange, onPrevious, onNext }: PaginationProps) => {
    const { t } = useTranslation(['common', 'users']);

    return (
        <div className="border-t border-gray-700/50 bg-gray-900/20 px-6 py-4 flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="text-sm text-gray-400">
                {meta.size > 0 && (
                    <span>
                        {t('users:user_table.pagination.showing_results', {
                            from: meta.page * meta.size + 1,
                            to: Math.min((meta.page + 1) * meta.size, meta.totalElements),
                            total: meta.totalElements,
                        })}
                    </span>
                )}
            </div>

            <div className="flex items-center gap-4">
                <div className="flex items-center gap-2 text-sm text-gray-400">
                    <Trans i18nKey="users:user_table.pagination.results_per_page">
                        Showing
                        <select
                            className="bg-gray-800 border border-gray-600 text-gray-200 text-sm rounded-lg focus:ring-indigo-500 focus:border-indigo-500 block p-1.5 cursor-pointer outline-none transition-shadow"
                            value={pageSize}
                            onChange={(e) => onPageSizeChange(Number(e.target.value))}
                        >
                            <option value="5">5</option>
                            <option value="10">10</option>
                            <option value="25">25</option>
                            <option value="50">50</option>
                            <option value="100">100</option>
                        </select>
                        results per page
                    </Trans>
                </div>

                <div className="flex gap-2">
                    <button
                        className="p-2 rounded-lg border border-gray-600 text-gray-400 cursor-pointer hover:bg-gray-700 hover:text-white disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        disabled={meta.page <= 0}
                        onClick={onPrevious}
                        title={t('users:user_table.pagination.previous')}
                    >
                        <ChevronLeft className="h-4 w-4" />
                    </button>
                    <button
                        className="p-2 rounded-lg border border-gray-600 text-gray-400 cursor-pointer hover:bg-gray-700 hover:text-white disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        disabled={meta.page >= meta.totalPages - 1}
                        onClick={onNext}
                        title={t('users:user_table.pagination.next')}
                    >
                        <ChevronRight className="h-4 w-4" />
                    </button>
                </div>
            </div>
        </div>
    );
};

interface UserTableProps {
    userPage?: PageResponse<User>;
    pageSize: number;
    editUser: (user: User) => void;
    deleteUser: (id: string) => void;
    setPageSize: (size: number) => void;
    previousPage: () => void;
    nextPage: () => void;
}

const UserTable = ({ userPage, pageSize, editUser, deleteUser, setPageSize, previousPage, nextPage }: UserTableProps) => {
    const { t, i18n } = useTranslation(['common', 'users']);

    return (
        <div className="w-full">
            <div className="overflow-x-auto">
                <table className="min-w-full">
                    <thead>
                        <tr className="bg-gray-500">
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-200 uppercase tracking-wider">
                                {t('users:user_table.user')}
                            </th>
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-200 uppercase tracking-wider">
                                {t('users:user_table.role')}
                            </th>
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-200 uppercase tracking-wider">
                                {t('users:user_table.created')}
                            </th>
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-200 uppercase tracking-wider">
                                {t('users:user_table.edit')}
                            </th>
                        </tr>
                    </thead>

                    <tbody className="divide-y divide-gray-700 bg-gray-800">
                        {userPage?.data.map((user: User) => (
                            <UserRow
                                key={user.id}
                                user={user}
                                locale={i18n.language}
                                onEdit={editUser}
                                onDelete={deleteUser}
                            />
                        ))}
                    </tbody>
                </table>
            </div>

            {userPage && (
                <Pagination
                    meta={userPage.meta}
                    pageSize={pageSize}
                    onPageSizeChange={setPageSize}
                    onPrevious={previousPage}
                    onNext={nextPage}
                />
            )}
        </div>
    );
};

export default UserTable;