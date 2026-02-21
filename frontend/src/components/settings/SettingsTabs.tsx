import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";

export interface TabDataProps {
    to: string;
    label: string;
}

const SettingsTabs = ({ tabs }: { tabs: TabDataProps[] }) => {
    const location = useLocation();
    const { t } = useTranslation();

    return (
        <nav className="mt-6 flex gap-8 overflow-x-scroll hide-scrollbar border-b border-gray-600">
            {tabs.map((tab) => (
                <Link
                    key={tab.to}
                    className={`px-1 py-4 font-medium leading-5 transition duration-300 border-b-2 whitespace-nowrap border-transparent
                                    ${location.pathname === tab.to
                        ? 'text-indigo-500 border-indigo-600'
                        : 'text-gray-500 hover:text-gray-300 hover:border-gray-400 focus:text-gray-300 focus:border-gray-400'
                    }`}
                    to={tab.to}
                >
                    {t(tab.label)}
                </Link>
            ))}
        </nav>
    )
}

export default SettingsTabs;