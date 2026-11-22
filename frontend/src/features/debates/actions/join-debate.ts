"use server";

import { redirect } from "next/navigation";
import type { InferSafeActionFnResult } from "next-safe-action";
import { z } from "zod";
import { actionClient } from "@/lib/apis/safe-action";
import { joinDebate as joinDebateRequest } from "../api/api";

export type JoinDebateActionResult = InferSafeActionFnResult<
	typeof joinDebate
>;

export const joinDebate = actionClient
	.inputSchema(z.object({
		debateId: z.uuid(),
		side: z.enum(["FOR", "AGAINST"]),
	}))
	.action(async ({ parsedInput: { debateId, side } }) => {
		await joinDebateRequest(debateId, side);
		redirect(`/debates/${debateId}/live`);
	});
