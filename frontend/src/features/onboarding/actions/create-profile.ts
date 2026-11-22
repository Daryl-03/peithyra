"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import type { InferSafeActionFnResult } from "next-safe-action";
import { ApiError } from "@/lib/apis/api-error";
import { backendFetch } from "@/lib/apis/backend-fetch";
import { authedClient } from "@/lib/apis/safe-action";
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

        if (!response.ok) throw await ApiError.fromResponse(response);

        revalidatePath("/", "layout");
        redirect("/agora");
    });
