interface FormActionsProps {
    onCancel: () => void;
    isSubmitting: boolean;
    submitLabel: string;
    loadingLabel: string;
    cancelLabel: string;
}

const FormActions = ({ onCancel, isSubmitting, submitLabel, loadingLabel, cancelLabel }: FormActionsProps) => (
    <div className="pt-4 flex justify-end gap-3">
        <button
            type="button"
            onClick={onCancel}
            className="px-4 py-2 text-sm font-medium text-gray-300 bg-gray-700/50 hover:bg-gray-700 rounded-lg transition-colors"
        >
            {cancelLabel}
        </button>
        <button
            type="submit"
            disabled={isSubmitting}
            className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-500 rounded-lg shadow-lg shadow-indigo-900/20 transition-all disabled:opacity-50"
        >
            {isSubmitting ? loadingLabel : submitLabel}
        </button>
    </div>
);

export default FormActions;
