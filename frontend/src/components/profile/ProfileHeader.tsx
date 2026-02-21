import { useTranslation } from "react-i18next";
import type { User } from "../../types/user";

interface ProfileHeaderProps {
    user: User;
}

const ProfileHeader = ({ user }: ProfileHeaderProps) => {
    const { t, i18n } = useTranslation(['common', 'settings']);

    return (
        <div className="flex items-end gap-6 mt-6 mb-12">
            <img
                src={`https://api.dicebear.com/9.x/shapes/svg?seed=${user?.id}`}
                alt="Avatar"
                className="h-24 w-24 rounded-full"
            />
            <div className="space-y-1">
                <h1 className="text-2xl font-bold bg-linear-to-br from-indigo-400 to-purple-400 text-transparent bg-clip-text">{user?.username}</h1>
                <p className="text-sm font-medium text-gray-400">{t('settings:user_header.join_date', {
                    joinDate: new Date(user.createdAt).toLocaleDateString(i18n.language, {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric'
                    })
                })}
                </p>
            </div>
        </div>
    )
}

export default ProfileHeader;