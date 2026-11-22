import "server-only";

import { notFound } from "next/navigation";
import { ApiError } from "@/lib/apis/api-error";
import { backendFetch } from "@/lib/apis/backend-fetch";
import type { DebateSummary } from "../model/debate-summary";
import type { DebateDetailsDTO } from "./debate-details-dto";
import type { DebateSummaryDTO } from "./debate-summary-dto";

export async function getLatestDebates(
    limit: number,
): Promise<DebateSummary[]> {
    const response = await fetch(
        `${process.env.BACKEND_URL}/api/debates?size=${limit}`,
        {
            cache: "no-cache",
        },
    ).catch((error: unknown) => {
        throw ApiError.fromUnknown(error);
    });
    if (!response.ok) throw await ApiError.fromResponse(response);
    const data = await response.json();
    return data.content.map((debate: DebateSummaryDTO) => ({
        id: debate.id,
        proposition: debate.proposition,
        status: debate.status,
        createdAt: debate.createdAt,
        participants: debate.participantViewList,
    }));
}

export async function getDebateById(
    debateId: string,
): Promise<DebateDetailsDTO> {
    const response = await fetch(
        `${process.env.BACKEND_URL}/api/debates/${debateId}`,
        {
            cache: "no-cache",
        },
    ).catch((error: unknown) => {
        throw ApiError.fromUnknown(error);
    });

    if (!response.ok) {
        const error = await ApiError.fromResponse(response);
        if (error.status === 404) notFound();
        throw error;
    }
    const data = await response.json();
    return data as DebateDetailsDTO;
}

export async function joinDebate(
    debateId: string,
    side: "FOR" | "AGAINST",
): Promise<void> {
    const response = await backendFetch(`/api/debates/${debateId}/join`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ side }),
    });

    if (!response.ok) throw await ApiError.fromResponse(response);
}

export async function toggleReadyStatus(debateId: string): Promise<void> {
    const response = await backendFetch(
        `/api/debates/${debateId}/ready/toggle`,
        {
            method: "POST",
        },
    );

    if (!response.ok) throw await ApiError.fromResponse(response);
}
