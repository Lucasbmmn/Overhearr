import { AlertTriangle } from 'lucide-react';
import { useTranslation } from 'react-i18next';
import Modal from './Modal';

interface ConfirmDialogProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
    title: string;
    message: string;
}

const ConfirmDialog = ({ isOpen, onClose, onConfirm, title, message }: ConfirmDialogProps) => {
    const { t } = useTranslation();

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={title}>
            <div className="p-6 space-y-4">
                <div className="flex items-center gap-3 text-red-500">
                    <AlertTriangle className="w-6 h-6 shrink-0" />
                    <p className="text-sm leading-5 text-gray-300">{message}</p>
                </div>

                <div className="flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 text-sm font-medium text-gray-300 bg-gray-700/50 hover:bg-gray-700 rounded-lg transition-colors"
                    >
                        {t('common.cancel')}
                    </button>
                    <button
                        onClick={onConfirm}
                        className="px-4 py-2 text-sm font-medium text-white bg-red-600/80 hover:bg-red-600 border border-red-500 rounded-lg transition-colors"
                    >
                        {t('common.delete')}
                    </button>
                </div>
            </div>
        </Modal>
    );
};

export default ConfirmDialog;
