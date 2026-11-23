"use server";

import type { InferSafeActionFnResult } from "next-safe-action";
import { z } from "zod";
import { actionClient } from "@/lib/apis/safe-action";
import { toggleReadyStatus } from "../api/api";

export type ToggleReadyActionResult = InferSafeActionFnResult<
    typeof toggleReadyAction
>;

export const toggleReadyAction = actionClient
    .inputSchema(z.object({ debateId: z.uuid() }))
    .action(async ({ parsedInput: { debateId } }) => {
        await toggleReadyStatus(debateId);
        return { success: true };
    });
