import { Languages } from 'lucide-react';
import React from 'react';
import { useTranslation } from 'react-i18next';

const LanguagePicker = () => {
    const { i18n } = useTranslation();

    const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        i18n.changeLanguage(e.target.value);
    };

    return (
        <div className="relative">
            <Languages className="h-4 w-4 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2 pointer-events-none" />

            <select
                value={i18n.language}
                onChange={handleLanguageChange}
                aria-label="Select Language"
                className="
                            appearance-none
                            bg-gray-900
                            border border-gray-600
                            text-gray-300
                            text-sm
                            rounded-full
                            py-1.5 pl-9 pr-4
                            focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                            cursor-pointer
                            hover:bg-gray-700
                            transition-colors
                        "
            >
                <option value="en">English</option>
                <option value="fr">Français</option>
            </select>
        </div>
    )
}

export default LanguagePicker;