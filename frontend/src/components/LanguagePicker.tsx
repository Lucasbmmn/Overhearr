import React from 'react';
import { useTranslation } from 'react-i18next';

const LanguagePicker = () => {
    const { i18n } = useTranslation();

    const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        i18n.changeLanguage(e.target.value);
    };

    return (
        <select
            value={i18n.language}
            onChange={handleLanguageChange}
            aria-label="Select Language"
            className="form-input-field"
        >
            <option value="en">English</option>
            <option value="fr">Français</option>
        </select>
    )
}

export default LanguagePicker;