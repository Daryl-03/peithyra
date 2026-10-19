import type { UseFormSetError } from "react-hook-form";
import { describe, expect, test, vi } from "vitest";
import { applyServerErrors } from "./apply-server-errors";

type Values = { proposition: string; description: string };
const fields = ["proposition", "description"] as const;

describe("applyServerErrors", () => {
    test("uses the first message per field and focuses only the first invalid field", () => {
        const setError = vi.fn<UseFormSetError<Values>>();
        applyServerErrors<Values>(setError, fields, {
            validationErrors: {
                proposition: { _errors: ["Titre invalide", "Autre erreur"] },
                description: { _errors: ["Description invalide"] },
            },
        });
        expect(setError.mock.calls).toEqual([
            [
                "proposition",
                { type: "server", message: "Titre invalide" },
                { shouldFocus: true },
            ],
            [
                "description",
                { type: "server", message: "Description invalide" },
                { shouldFocus: false },
            ],
        ]);
    });

    test("skips fields without errors and preserves focus for the first actual error", () => {
        const setError = vi.fn<UseFormSetError<Values>>();
        applyServerErrors<Values>(setError, fields, {
            validationErrors: {
                proposition: { _errors: [] },
                description: { _errors: ["Description invalide"] },
                _errors: ["Erreur globale"],
            },
        });
        expect(setError.mock.calls).toEqual([
            [
                "description",
                { type: "server", message: "Description invalide" },
                { shouldFocus: true },
            ],
            ["root.server", { type: "server", message: "Erreur globale" }],
        ]);
    });

    test("does not invent a global error for an empty validation result", () => {
        const setError = vi.fn<UseFormSetError<Values>>();
        applyServerErrors<Values>(setError, fields, { validationErrors: {} });
        expect(setError).not.toHaveBeenCalled();
    });

    test.each([
        {
            error: { serverError: "Service indisponible" },
            message: "Service indisponible",
        },
        {
            error: {},
            message: "Impossible de joindre le serveur. Veuillez réessayer.",
        },
    ])("shows a global server error or fallback: $message", ({
        error,
        message,
    }) => {
        const setError = vi.fn<UseFormSetError<Values>>();
        applyServerErrors<Values>(setError, fields, error);
        expect(setError).toHaveBeenCalledExactlyOnceWith("root.server", {
            type: "server",
            message,
        });
    });
});
