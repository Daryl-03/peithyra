"use server";

import { z } from "zod";
import { toggleReadyStatus } from "../api/api";
import { actionClient } from "@/lib/apis/safe-action";
import type { InferSafeActionFnResult } from "next-safe-action";

export type ToggleReadyActionResult = InferSafeActionFnResult<
    typeof toggleReadyAction
>;

export const toggleReadyAction = actionClient
    .inputSchema(z.object({ debateId: z.uuid() }))
    .action(async ({ parsedInput: { debateId } }) => {
        await toggleReadyStatus(debateId);
        return { success: true };
    });
