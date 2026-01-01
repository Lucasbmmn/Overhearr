import { Trans, useTranslation } from 'react-i18next';
import LanguagePicker from '../components/LanguagePicker';
import { useAuthStore } from '../store/authStore';

const Dashboard = () => {
    const { user, logout } = useAuthStore();
    const { t } = useTranslation();
    return (
        <div className="min-h-screen p-8 bg-gray-900 text-white">
            <div className="absolute top-4 right-4 z-50">
                <LanguagePicker />
            </div>
            <h1 className="text-2xl mb-4">
                <Trans i18nKey="dashboard.welcome_user" values={{ username: user?.username }}>
                    Welcome, <span className="text-blue-400">user</span>!
                </Trans>
            </h1>
            <p className="mb-4 text-gray-400">{t("dashboard.role_display", { role: user?.role })}</p>
            <button onClick={logout} className="title px-4 py-2 bg-red-600 rounded hover:bg-red-700">{t("auth.logout")}</button>
        </div>
    );
};

export default Dashboard;