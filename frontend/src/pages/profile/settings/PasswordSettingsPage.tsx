import { zodResolver } from '@hookform/resolvers/zod';
import { Check, Save, Lock } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import SecretInput from '../../../components/ui/SecretInput';
import { useUserSchemas } from '../../../schemas/userSchemas';
import { userService } from '../../../services/userService';

const PasswordSettingsPage = () => {
    const { t } = useTranslation(['common', 'settings']);
    const { passwordRulesSchema } = useUserSchemas();

    const passwordSchema = z.object({
        currentPassword: z.string().min(1, t('validation.current_password_required')),
        newPassword: passwordRulesSchema,
        confirmPassword: z.string(),
    }).refine((data) => data.newPassword === data.confirmPassword, {
        message: t('validation.password_confirm'),
        path: ["confirmPassword"],
    });

    type PasswordForm = z.infer<typeof passwordSchema>;

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting, isSubmitSuccessful },
    } = useForm<PasswordForm>({
        resolver: zodResolver(passwordSchema),
    });

    const onSubmit = async (data: PasswordForm) => {
        try {
            await userService.updateMyPassword({
                password: data.currentPassword,
                newPassword: data.newPassword,
            });
            reset();
        } catch (error) {
            console.error("Failed to update password", error);
        }
    };

    return (
        <>
            <h2 className="text-2xl font-bold">{t('settings:password.title')}</h2>

            <form onSubmit={handleSubmit(onSubmit)}>
                <div className="form-row">
                    <label className="text-label">{t('settings:password.current_password')}</label>
                    <div className="col-span-2 w-full max-w-xl">
                        <SecretInput
                            {...register("currentPassword")}
                            leftIcon={<Lock className="h-5 w-5 text-gray-500" />}
                        />
                        {errors.currentPassword &&
                            <p className="text-sm text-red-400 mt-1">{errors.currentPassword.message}</p>
                        }
                    </div>
                </div>

                <div className="form-row">
                    <label className="text-label">{t('settings:password.new_password')}</label>
                    <div className="col-span-2 w-full max-w-xl">
                        <SecretInput
                            {...register("newPassword")}
                            leftIcon={<Lock className="h-5 w-5 text-gray-500" />}
                        />
                        {errors.newPassword &&
                            <p className="text-sm text-red-400 mt-1">{errors.newPassword.message}</p>
                        }
                    </div>
                </div>

                <div className="form-row">
                    <label className="text-label">{t('settings:password.confirm_password')}</label>
                    <div className="col-span-2 w-full max-w-xl">
                        <SecretInput
                            {...register("confirmPassword")}
                            leftIcon={<Lock className="h-5 w-5 text-gray-500" />}
                        />
                        {errors.confirmPassword &&
                            <p className="text-sm text-red-400 mt-1">{errors.confirmPassword.message}</p>
                        }
                    </div>
                </div>

                <div className="actions flex justify-end items-center gap-4">
                    {isSubmitSuccessful && (
                        <span className="flex items-center gap-2 text-green-400 text-sm animate-fade-in">
                            <Check className="w-4 h-4" />
                            {t('settings:password.success')}
                        </span>
                    )}
                    <button
                        type="submit"
                        disabled={isSubmitting}
                        className="flex gap-2 items-center justify-center px-4 py-2 text-sm font-semibold rounded-md transition ease-in-out duration-150 cursor-pointer disabled:opacity-50 whitespace-nowrap outline-none bg-indigo-600/80 border border-indigo-500 hover:bg-indigo-600 focus:border-indigo-700"
                    >
                        <Save className="w-4 h-4" />
                        <span>{isSubmitting ? t('app.loading') : t('common.save')}</span>
                    </button>
                </div>
            </form>
        </>
    );
};

export default PasswordSettingsPage;