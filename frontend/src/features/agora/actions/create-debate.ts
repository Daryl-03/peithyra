"use server";

import { revalidatePath } from "next/cache";
import type { InferSafeActionFnResult } from "next-safe-action";
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

        if (!response.ok) {
            console.error(
                `Debate creation failed with status ${response.status}: ${await response.text()}`,
            );
            throw new Error(
                `Debate creation failed with status ${response.status}`,
            );
        }

        const debate = await response.json();
        revalidatePath("/agora");
        return debate;
    });
