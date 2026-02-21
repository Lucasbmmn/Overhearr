import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import { UserRole } from '../types/user';

export const useUserSchemas = () => {
    const { t } = useTranslation();

    const passwordRulesSchema = z.string()
        .min(8, t('validation.password_length'))
        .regex(/.*[A-Z].*/, t('validation.password_uppercase'))
        .regex(/.*[a-z].*/, t('validation.password_lowercase'))
        .regex(/.*\d.*/, t('validation.password_digit'))
        .regex(/[^A-Za-z0-9]/, t('validation.password_special'));

    const userBaseSchema = z.object({
        username: z.string()
            .min(3, t('validation.username_length'))
            .regex(/^[a-zA-Z0-9](?:[a-zA-Z0-9]|[._](?![._]))*[a-zA-Z0-9]$/, t('validation.username_pattern')),
        email: z.email(t('validation.email_invalid')).optional().or(z.literal("")),
        role: z.enum(UserRole),
    });

    const createUserSchema = userBaseSchema.extend({
        password: passwordRulesSchema,
    });

    const editUserSchema = userBaseSchema;

    return { createUserSchema, editUserSchema, passwordRulesSchema };
};
