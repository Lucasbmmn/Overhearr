import type { ReactNode } from 'react';

interface FormFieldProps {
    label: string;
    error?: string;
    children: ReactNode;
}

const FormField = ({ label, error, children }: FormFieldProps) => (
    <div className="space-y-2">
        <label className="text-sm font-medium text-gray-300">{label}</label>
        {children}
        {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
);

export default FormField;
