import { Eye, EyeOff } from 'lucide-react';
import { forwardRef, useState, type InputHTMLAttributes, type ReactNode } from 'react';
import { INPUT_CLASS } from '../../lib/styles';

interface SecretInputProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type'> {
    leftIcon?: ReactNode;
    wrapperClassName?: string;
}

const SecretInput = forwardRef<HTMLInputElement, SecretInputProps>(
    ({ className, leftIcon, wrapperClassName, ...props }, ref) => {
        const [visible, setVisible] = useState(false);

        return (
            <div className={`relative w-full ${wrapperClassName || ''}`}>
                {leftIcon && (
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        {leftIcon}
                    </div>
                )}
                <input
                    ref={ref}
                    type={visible ? 'text' : 'password'}
                    className={`${className ?? INPUT_CLASS} ${leftIcon ? 'pl-10' : ''} pr-12`}
                    {...props}
                />
                <button
                    type="button"
                    onClick={() => setVisible(v => !v)}
                    className="absolute inset-y-0 right-0 px-3 cursor-pointer bg-indigo-600 hover:bg-indigo-600 text-white rounded-r-lg transition-colors focus:outline-none flex items-center justify-center border border-indigo-500"
                    tabIndex={-1}
                >
                    {visible ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                </button>
            </div>
        );
    }
);

SecretInput.displayName = 'SecretInput';

export default SecretInput;
