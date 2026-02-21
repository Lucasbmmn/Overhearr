import { zodResolver } from '@hookform/resolvers/zod';
import { isAxiosError } from 'axios';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { Lock, User as UserIcon, AlertCircle, Loader2 } from 'lucide-react';
import { z } from 'zod';
import LanguagePicker from '../components/LanguagePicker';
import SecretInput from '../components/ui/SecretInput';
import { useAuthStore } from '../store/authStore';

const loginSchema = z.object({
    identifier: z.string(),
    password: z.string(),
});

type LoginFormInputs = z.infer<typeof loginSchema>;

const Login = () => {
    const { t } = useTranslation(['common', 'auth']);
    const navigate = useNavigate();
    const { login, isLoading, errorKey, clearError } = useAuthStore();

    const {
        register,
        handleSubmit,
        setError,
        setFocus,
    } = useForm<LoginFormInputs>({
        resolver: zodResolver(loginSchema),
    });

    const onSubmit = async (data: LoginFormInputs) => {
        try {
            await login(data);
            navigate('/');
        } catch (err) {
            console.error('Login failed', err);

            if (isAxiosError(err) && err.response?.data?.field) {
                setError(err.response.data.field, {
                    type: 'server',
                    message: err.response.data.message
                });
            }

            setFocus('identifier');
        }
    }

    useEffect(() => {
        return () => clearError();
    }, [clearError]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-900 px-4">
            <div className="max-w-md w-full bg-gray-800 border border-gray-700 rounded-lg shadow-xl p-8">
                <div className="absolute top-4 right-4 z-50">
                    <LanguagePicker />
                </div>

                <div className="text-center mb-8">
                    <h1 className="text-5xl font-bold text-indigo-500">{t("app.title")}</h1>
                    <p className="text-gray-400 mt-2">{t("auth:subtitle")}</p>
                </div>

                {errorKey && (
                    <div className="mb-4 p-3 bg-red-500/10 border border-red-500/50 rounded flex items-center gap-2 text-red-400 text-sm">
                        <AlertCircle size={18} />
                        <span>{t(errorKey)}</span>
                    </div>
                )}

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-300 mb-2">{t("auth:identifier")}</label>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <UserIcon className="h-5 w-5 text-gray-500" />
                            </div>
                            <input
                                {...register("identifier")}
                                type="text"
                                name="identifier"
                                required
                                className="block w-full pl-10 pr-3 py-2.5 bg-gray-900 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-indigo-500  focus:border-transparent"
                                placeholder={t("auth:identifier_placeholder")}
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-300 mb-2">{t("auth:password")}</label>
                        <SecretInput
                            {...register("password")}
                            leftIcon={<Lock className="h-5 w-5 text-gray-500" />}
                            required
                            className="block w-full py-2.5 bg-gray-900 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent placeholder-gray-500"
                            placeholder={t("auth:password_placeholder")}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full flex justify-center py-2.5 px-4 cursor-pointer rounded-lg shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-400 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                    >
                        {isLoading ? <Loader2 className="animate-spin h-5 w-5" /> : t("auth:sign_in")}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;