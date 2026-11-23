"use server";

import { redirect } from "next/navigation";
import type { InferSafeActionFnResult } from "next-safe-action";
import { z } from "zod";
import { ApiError } from "@/lib/apis/api-error";
import { backendFetch } from "@/lib/apis/backend-fetch";
import { actionClient } from "@/lib/apis/safe-action";

export type LeaveDebateActionResult = InferSafeActionFnResult<
    typeof leaveDebate
>;

export const leaveDebate = actionClient
    .inputSchema(z.object({ debateId: z.uuid() }))
    .action(async ({ parsedInput: { debateId } }) => {
        const response = await backendFetch(`/api/debates/${debateId}/leave`, {
            method: "POST",
        });

        if (!response.ok) throw await ApiError.fromResponse(response);

        redirect("/agora");
    });
