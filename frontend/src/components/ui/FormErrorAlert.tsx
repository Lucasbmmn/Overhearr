interface FormErrorAlertProps {
    message?: string;
}

const FormErrorAlert = ({ message }: FormErrorAlertProps) => {
    if (!message) return null;

    return (
        <div className="p-3 rounded bg-red-500/10 border border-red-500/20 text-red-400 text-sm">
            {message}
        </div>
    );
};

export default FormErrorAlert;
