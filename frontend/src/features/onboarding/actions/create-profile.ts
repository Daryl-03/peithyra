"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import type { InferSafeActionFnResult } from "next-safe-action";
import { backendFetch } from "@/lib/backend-fetch";
import { ActionError, authedClient } from "@/lib/safe-action";
import { onboardingFormSchema } from "../model/onboarding-schema";

export type CreateProfileActionResult = InferSafeActionFnResult<
    typeof createProfile
>;

export const createProfile = authedClient
    .inputSchema(onboardingFormSchema)
    .action(async ({ parsedInput: { username } }) => {
        const response = await backendFetch("/identity/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: new URLSearchParams({ username }),
            cache: "no-store",
            signal: AbortSignal.timeout(10000),
        });

        if (response.status === 409) {
            throw new ActionError(
                "Ce pseudo ou ce compte est déjà enregistré. Essayez un autre pseudo ou revenez aux débats.",
            );
        }
        if (!response.ok) {
            throw new Error(
                `Profile creation failed with status ${response.status}`,
            );
        }

        revalidatePath("/", "layout");
        redirect("/agora");
    });
