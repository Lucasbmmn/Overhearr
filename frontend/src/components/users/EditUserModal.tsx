import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import type { z } from "zod";
import { INPUT_CLASS, SELECT_CLASS } from "../../lib/styles";
import { useUserSchemas } from "../../schemas/userSchemas";
import { userService } from "../../services/userService";
import { UserRole, type User } from "../../types/user";
import FormActions from "../ui/FormActions";
import FormErrorAlert from "../ui/FormErrorAlert";
import FormField from "../ui/FormField";
import Modal from "../ui/Modal";

interface EditUserModalProps {
    isOpen: boolean;
    user: User | undefined;
    onClose: () => void;
    onSuccess: () => void;
}

const EditUserModal = ({ isOpen, user, onClose, onSuccess }: EditUserModalProps) => {
    const { t } = useTranslation(['common', 'users']);
    const { editUserSchema } = useUserSchemas();

    type EditUserForm = z.infer<typeof editUserSchema>;

    const {
        register,
        handleSubmit,
        setError,
        reset,
        formState: { errors, isSubmitting },
    } = useForm<EditUserForm>({
        resolver: zodResolver(editUserSchema),
    });

    useEffect(() => {
        if (user) {
            reset({
                username: user.username,
                email: user.email || "",
                role: user.role,
            });
        }
    }, [user, reset]);

    if (!isOpen || !user) return null;

    const onSubmit = async (data: EditUserForm) => {
        try {
            await userService.updateUserById(user.id, {
                username: data.username,
                email: data.email || null,
                role: data.role,
            });
            onSuccess();
        } catch (error) {
            console.error(error);
            setError("root", {
                message: t('app.internal_server_error')
            });
        }
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={t('users:user_table.edit_user')}>
            <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-4">
                <FormField label={t('users:edit.username')} error={errors.username?.message}>
                    <input
                        {...register("username")}
                        className={INPUT_CLASS}
                    />
                </FormField>

                <FormField label={t('users:edit.email')} error={errors.email?.message}>
                    <input
                        {...register("email")}
                        type="email"
                        className={INPUT_CLASS}
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

                <FormErrorAlert message={errors.root?.message} />

                <FormActions
                    onCancel={onClose}
                    isSubmitting={isSubmitting}
                    submitLabel={t('common.save')}
                    loadingLabel={t('app.loading')}
                    cancelLabel={t('common.cancel')}
                />
            </form>
        </Modal>
    );
};

export default EditUserModal;