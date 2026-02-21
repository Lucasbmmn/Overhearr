import { SparklesIcon, UsersRound } from 'lucide-react';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { UserRole } from '../../types/user';

interface SidebarProps {
    isOpen: boolean;
    onClose: () => void;
}

interface SidebarLinkProps {
    to: string;
    icon: React.ReactNode;
    label: string;
    permissions?: UserRole;
}

const SidebarLinks: SidebarLinkProps[] = [
    {
        to: '/',
        icon: <SparklesIcon className="h-6 w-6" />,
        label: 'components.layout.sidebar.discover',
    },
    {
        to: '/users',
        icon: <UsersRound className="h-6 w-6" />,
        label: 'components.layout.sidebar.users',
        permissions: UserRole.ADMIN,
    }
];

const Sidebar = ({ isOpen, onClose }: SidebarProps) => {
    const { t } = useTranslation();
    const location = useLocation();
    const { user } = useAuthStore();

    const renderLinks = () => SidebarLinks
        .filter((link) => !link.permissions || link.permissions === user?.role)
        .map((sidebarLink) => (
            <Link
                key={sidebarLink.to}
                onClick={onClose}
                className={`flex gap-3 items-center p-2 rounded-md text-lg font-semibold leading-6 text-white transition duration-150 ease-in-out
            ${location.pathname === sidebarLink.to
                    ? 'bg-linear-to-br from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500'
                    : 'hover:bg-gray-700'
                }`}
                to={sidebarLink.to}
            >
                {sidebarLink.icon}
                {t(sidebarLink.label)}
            </Link>
        ));

    return (
        <>
            {isOpen && (
                <div
                    className="fixed inset-0 bg-black/50 z-40 lg:hidden"
                    onClick={onClose}
                />
            )}
            <aside className={`
                fixed top-0 bottom-0 left-0 z-30 hidden lg:flex w-64 bg-linear-to-b from-gray-800 to-gray-900 border-r border-r-gray-700 flex-col overflow-y-scroll gap-10 px-4 py-2
            `}>
                <div className="flex justify-center items-center w-full text-4xl font-bold bg-linear-to-br from-indigo-500 to-purple-500 text-transparent bg-clip-text">
                    <Link to="/" className="h-24 flex items-center">
                        {t("app.title")}
                    </Link>
                </div>
                <nav className="flex-1 flex flex-col gap-4">
                    {renderLinks()}
                </nav>
            </aside>
        </>
    )
}

export default Sidebar;