import { z } from "zod";

export const onboardingFormSchema = z.object({
    username: z
        .string()
        .trim()
        .min(5, "Votre pseudo doit contenir au moins 5 caractères.")
        .max(50, "Votre pseudo doit contenir au maximum 50 caractères."),
});
