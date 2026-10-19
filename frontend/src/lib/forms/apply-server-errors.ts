import type { FieldPath, FieldValues, UseFormSetError } from "react-hook-form";

type ServerValidationErrors<T extends FieldValues> = {
    _errors?: string[];
} & Partial<Record<FieldPath<T>, { _errors?: string[] }>>;

type ServerFormError<T extends FieldValues> = {
    validationErrors?: ServerValidationErrors<T>;
    serverError?: string;
};

export function applyServerErrors<T extends FieldValues>(
    setError: UseFormSetError<T>,
    fields: readonly FieldPath<T>[],
    error: ServerFormError<T>,
) {
    if (error.validationErrors) {
        const errors = error.validationErrors;
        let shouldFocus = true;

        for (const name of fields) {
            const message = errors[name]?._errors?.[0];

            if (message) {
                setError(name, { type: "server", message }, { shouldFocus });
                shouldFocus = false;
            }
        }

        const message = errors._errors?.[0];

        if (message) {
            setError("root.server", { type: "server", message });
        }

        return;
    }

    setError("root.server", {
        type: "server",
        message:
            error.serverError ??
            "Impossible de joindre le serveur. Veuillez réessayer.",
    });
}
