import z from "zod";
import { DebateSide } from "./debate-side";

export const createDebateFormSchema = z.object({
    proposition: z
        .string()
        .trim()
        .min(15, "Le titre doit contenir au moins 15 caractères.")
        .max(100, "Le titre doit contenir au maximum 100 caractères."),
    description: z
        .string()
        .trim()
        .min(10, "La description doit contenir au moins 10 caractères.")
        .max(200, "La description doit contenir au maximum 200 caractères."),
    side: z.enum(DebateSide, "Vous devez choisir un camp."),
    numberOfTurns: z
        .number()
        .int()
        .min(1, "Le nombre de tours doit être au moins 1.")
        .max(10, "Le nombre de tours doit être au maximum 10."),
    turnTimeLimitSeconds: z
        .number()
        .int()
        .min(60, "Le temps par tour doit être au moins 30 secondes.")
        .max(600, "Le temps par tour doit être au maximum 600 secondes."),
});

export const defaultCreateDebateValues = {
    proposition: "",
    description: "",
    side: DebateSide.For,
    numberOfTurns: 3,
    turnTimeLimitSeconds: 180,
};
