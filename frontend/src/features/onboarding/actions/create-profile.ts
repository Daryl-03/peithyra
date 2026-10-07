"use server";

import { backendFetch } from "@/lib/backend-fetch";
import { authedClient } from "@/lib/safe-action";
import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { onboardingFormDataSchema, onboardingFormSchema } from "../model/onboarding-schema";
import type { InferSafeActionFnResult } from "next-safe-action";

// export type OnboardingState = { error?: string; username?: string };

export type CreateProfileActionResult = InferSafeActionFnResult<typeof createProfile>;

export const createProfile = authedClient
	.inputSchema(onboardingFormDataSchema)
	.stateAction(async ({ parsedInput }, _previousState) => {
		const { username } = parsedInput;
		
		try {
			const response = await backendFetch(`/identity/register`, {
				method: "POST",
				headers: {
					"Content-Type": "application/x-www-form-urlencoded",
				},
				body: new URLSearchParams({ username }),
				cache: "no-store",
				signal: AbortSignal.timeout(10000),
			});
			if (!response.ok)
				return {
					username,
					error:
						response.status === 409
							? "Ce pseudo ou ce compte est déjà enregistré. Essayez un autre pseudo ou revenez aux débats."
							: response.status === 401
								? "Votre session a expiré. Reconnectez-vous pour continuer."
								: "Impossible de créer votre profil pour le moment. Veuillez réessayer.",
				};
		} catch {
			return {
				username,
				error: "Le service est momentanément indisponible. Votre pseudo a été conservé, vous pouvez réessayer.",
			};
		}
		revalidatePath("/", "layout");
		redirect("/");
	});
