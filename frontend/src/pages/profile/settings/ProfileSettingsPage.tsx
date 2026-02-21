import { zodResolver } from '@hookform/resolvers/zod';
import { Save } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import LanguagePicker from "../../../components/LanguagePicker";
import { useAuthStore } from '../../../store/authStore';
import { userService } from '../../../services/userService';
import { useEffect } from 'react';

const ProfileSettingsPage = () => {
    const { t } = useTranslation(['common', 'settings']);
    const { user, setUser } = useAuthStore();

    const profilUpdateSchema = z.object({
        username: z.string().min(3, t('validation.username_length')),
        email: z.email(t('validation.email_invalid')).optional().or(z.literal('')),
    });

    type ProfilUpdateForm = z.infer<typeof profilUpdateSchema>;

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting, isDirty },
    } = useForm<ProfilUpdateForm>({
        resolver: zodResolver(profilUpdateSchema),
        defaultValues: {
            username: user?.username || '',
            email: user?.email || '',
        }
    });

    useEffect(() => {
        if (user) {
            reset({ username: user.username, email: user.email || '' });
        }
    }, [user, reset]);

    const onSubmit = async (data: ProfilUpdateForm) => {
        try {
            const updateUser = await userService.updateMyself({
                username: data.username,
                email: data.email || null,
            });
            setUser(updateUser);
        } catch (error) {
            console.error('Failed to update profile', error);
        }
    };

    return (
        <>
            <h2 className="text-2xl font-bold">{t('settings:general.title')}</h2>

            <div className="form-row">
                <label className="text-label">{t('settings:general.display_language')}</label>
                <LanguagePicker />
            </div>

            <form onSubmit={handleSubmit(onSubmit)}>
                <div className="form-row">
                    <label className="text-label">{t('settings:general.username')}</label>
                    <input
                        {...register("username")}
                        className="form-input-field"
                        type="text"
                    />
                    {errors.username &&
                        <p className="text-sm text-red-400">{errors.username.message}</p>
                    }
                </div>

                <div className="form-row">
                    <label className="text-label">{t('settings:general.email')}</label>
                    <input
                        {...register("email")}
                        className="form-input-field"
                        type="email"
                    />
                    {errors.email &&
                        <p className="text-xs text-red-400">{errors.email.message}</p>
                    }
                </div>


                <div className="actions flex justify-end">
                    <button
                        type="submit"
                        disabled={!isDirty || isSubmitting}
                        className="flex gap-2 items-center justify-center px-4 py-2 text-sm font-semibold rounded-md transition ease-in-out duration-150 cursor-pointer disabled:opacity-50 whitespace-nowrap outline-none bg-indigo-600/80 border border-indigo-500 hover:bg-indigo-600 focus:border-indigo-700"
                    >
                        <Save className="w-4 h-4" />
                        <span>{isSubmitting ? t('app.loading') : t('common.save')}</span>
                    </button>
                </div>
            </form>
        </>
    );
}

export default ProfileSettingsPage;