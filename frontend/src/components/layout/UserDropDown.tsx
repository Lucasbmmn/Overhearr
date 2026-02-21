import { LogOut, Settings } from 'lucide-react';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';

interface MenuLinkProps {
    to: string;
    icon: React.ReactNode;
    label: string;
}

const MenuLinks: MenuLinkProps[] = [
    {
        to: '/profile/settings',
        icon: <Settings className="h-5 w-5" />,
        label: 'components.layout.user_dropdown.settings',
    }
];

const UserDropDown = () => {
    const { t } = useTranslation();
    const { user } = useAuthStore();
    const { logout } = useAuthStore();
    const [isOpen, setIsOpen] = React.useState(false);

    const dropdownRef = React.useRef<HTMLDivElement>(null);

    React.useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (
                dropdownRef.current &&
                event.target instanceof Node &&
                !dropdownRef.current.contains(event.target)
            ) {
                setIsOpen(false);
            }
        }
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        }
    }, [])

    const toggleMenu = () => setIsOpen(!isOpen);

    const profilImage = () => (
        <img alt="Profil image" src={`https://api.dicebear.com/9.x/shapes/svg?seed=${user?.id}`} className="h-10 w-10 rounded-full object-center object-cover" />
    );

    const RendersLinks = () => MenuLinks
        .map((sidebarLink) => (
            <Link
                key={sidebarLink.to}
                onClick={() => setIsOpen(false)}
                className="flex gap-3 items-center px-4 py-2 rounded-md text-sm font-medium transition duration-150 ease-in-out bg-linear-to-br hover:from-indigo-600 hover:to-purple-600"
                to={sidebarLink.to}
            >
                {sidebarLink.icon}
                {t(sidebarLink.label)}
            </Link>
        ));

    return (
        <div className="relative" ref={dropdownRef}>
            <button
                className="flex shrink-0 rounded-full ring-1 cursor-pointer ring-gray-700 hover:ring-gray-500 focus:outline-none focus:ring-gray-500"
                onClick={toggleMenu}
            >
                {profilImage()}
            </button>
            {isOpen && (
                <div className="absolute right-0 mt-2 w-72 origin-top-right rounded-md shadow-lg divide-y divide-gray-700 bg-gray-800/80 ring-1 ring-gray-700 backdrop-blur">
                    <div className="flex gap-2 p-4 items-center">
                        {profilImage()}
                        <div className="flex flex-col min-w-0">
                            <span className="truncate text-xl font-semibold">{user?.username}</span>
                            {user?.email && <span className="truncate text-sm text-gray-400">{user.email}</span>}
                        </div>
                    </div>
                    <div className="flex flex-col min-w-0 p-1">
                        {RendersLinks()}
                        <button
                            className="flex gap-3 items-center px-4 py-2 cursor-pointer rounded-md text-sm font-medium transition duration-150 ease-in-out bg-linear-to-br hover:from-indigo-600 hover:to-purple-600"
                            onClick={logout}
                        >
                            <LogOut className="h-5 w-5" />
                            <span>{t("components.layout.user_dropdown.logout")}</span>
                        </button>
                    </div>
                </div>
            )}
        </div>
    )
}

export default UserDropDown;