"use server";

import { redirect } from "next/navigation";
import type { InferSafeActionFnResult } from "next-safe-action";
import { ApiError } from "@/lib/apis/api-error";
import { backendFetch } from "@/lib/apis/backend-fetch";
import { authedClient } from "@/lib/apis/safe-action";
import { createDebateFormSchema } from "../models/create-debate-schema";

export type CreateDebateActionResult = InferSafeActionFnResult<
    typeof createDebate
>;

export const createDebate = authedClient
    .inputSchema(createDebateFormSchema)
    .action(async ({ parsedInput }) => {
        const response = await backendFetch("/api/debates", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(parsedInput),
        });

        if (!response.ok) throw await ApiError.fromResponse(response);

        const debate = await response.json();
        // revalidatePath("/agora");
        redirect(`/debates/${debate.id}`);
        return debate;
    });
