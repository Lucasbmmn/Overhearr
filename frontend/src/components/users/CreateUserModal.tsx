import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import type { z } from 'zod';
import { INPUT_CLASS, SELECT_CLASS } from '../../lib/styles';
import { useUserSchemas } from '../../schemas/userSchemas';
import { userService } from '../../services/userService';
import { UserRole } from '../../types/user';
import FormActions from '../ui/FormActions';
import FormErrorAlert from '../ui/FormErrorAlert';
import FormField from '../ui/FormField';
import Modal from '../ui/Modal';
import SecretInput from '../ui/SecretInput';

interface CreateUserModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

const CreateUserModal = ({ isOpen, onClose, onSuccess }: CreateUserModalProps) => {
    const { t } = useTranslation(['common', 'users']);
    const { createUserSchema } = useUserSchemas();

    type CreateUserForm = z.infer<typeof createUserSchema>;

    const {
        register,
        handleSubmit,
        setError,
        reset,
        formState: { errors, isSubmitting },
    } = useForm<CreateUserForm>({
        resolver: zodResolver(createUserSchema),
        defaultValues: {
            role: UserRole.USER,
        },
    });

    if (!isOpen) return null;

    const onSubmit = async (data: CreateUserForm) => {
        try {
            await userService.createUser({
                username: data.username,
                email: data.email || null,
                role: data.role,
                password: data.password,
            });
            reset();
            onSuccess();
        } catch (error) {
            console.error(error);
            setError('root', {
                message: t('app.internal_server_error')
            });
        }
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={t('users:user_table.create_user')}>
            <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-4">
                <FormField label={t('users:edit.username')} error={errors.username?.message}>
                    <input
                        {...register("username")}
                        className={INPUT_CLASS}
                        placeholder="jdoe"
                    />
                </FormField>

                <FormField label={t('users:edit.email')} error={errors.email?.message}>
                    <input
                        {...register("email")}
                        type="email"
                        className={INPUT_CLASS}
                        placeholder="john@example.com"
                    />
                </FormField>

                <FormField label={t('users:user_table.role')}>
                    <select
                        {...register("role")}
                        className={SELECT_CLASS}
                    >
                        <option value={UserRole.USER}>{t('users:user_table.user')}</option>
                        <option value={UserRole.ADMIN}>{t('users:user_table.admin')}</option>
                    </select>
                </FormField>

                <FormField label={t('users:password')} error={errors.password?.message}>
                    <SecretInput
                        {...register("password")}
                        placeholder="••••••••"
                    />
                </FormField>

                <FormErrorAlert message={errors.root?.message} />

                <FormActions
                    onCancel={onClose}
                    isSubmitting={isSubmitting}
                    submitLabel={t('common.create')}
                    loadingLabel={t('app.loading')}
                    cancelLabel={t('common.cancel')}
                />
            </form>
        </Modal>
    );
};

export default CreateUserModal;