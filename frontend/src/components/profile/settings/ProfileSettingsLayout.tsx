import { Outlet } from "react-router-dom";
import { useAuthStore } from "../../../store/authStore";
import SettingsTabs, { type TabDataProps } from "../../settings/SettingsTabs";
import ProfileHeader from "../ProfileHeader";

const Tabs: TabDataProps[] = [
    {
        to: '/profile/settings',
        label: 'settings:layout.general',
    },
    {
        to: '/profile/settings/password',
        label: 'settings:layout.password',
    }
]

const ProfileSettingsLayout = () => {
    const { user } = useAuthStore();

    return (
        <>
            {user && (
                <ProfileHeader user={user} />
            )}
            <SettingsTabs tabs={Tabs} />
            <div className="mt-10">
                <Outlet />
            </div>
        </>
    );
};

export default ProfileSettingsLayout;