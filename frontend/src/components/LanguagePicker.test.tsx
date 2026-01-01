import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import LanguagePicker from './LanguagePicker';

const mockChangeLanguage = vi.fn();

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        i18n: {
            language: 'en',
            changeLanguage: mockChangeLanguage,
        },
    }),
}));

describe('LanguagePicker', () => {
    it('should render with current language selected', () => {
        render(<LanguagePicker />);
        const select = screen.getByRole('combobox', { name: 'Select Language' });

        fireEvent.change(select, { target: { value: 'fr' } });

        expect(mockChangeLanguage).toHaveBeenCalledWith('fr')
    });
});