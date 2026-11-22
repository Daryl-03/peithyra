import { backendFetch } from "@/lib/apis/backend-fetch";
import type { DebateSummary } from "../model/debate-summary";
import type { DebateDetailsDTO } from "./debate-details-dto";
import type { DebateSummaryDTO } from "./debate-summary-dto";
import { red } from "next/dist/lib/picocolors";
import { notFound, redirect } from "next/navigation";

export async function getLatestDebates(
	limit: number,
): Promise<DebateSummary[]> {
	try {
		const response = await fetch(`${process.env.BACKEND_URL}/api/debates?size=${limit}`, {
			cache: "no-cache"
		});

		if (!response.ok) {
			throw new Error(
				`Failed to fetch latest debates: ${response.statusText}`,
			);
		}
		const data = await response.json();
		const debates: DebateSummary[] = data.content.map(
			(debate: DebateSummaryDTO) => ({
				id: debate.id,
				proposition: debate.proposition,
				status: debate.status,
				createdAt: debate.createdAt,
				participants: debate.participantViewList,
			}),
		);
		return debates;
	} catch (error) {
		console.error("Error fetching latest debates:", error);
		throw error;
	}
}

export async function getDebateById(
	debateId: string,
): Promise<DebateDetailsDTO> {
	const response = await fetch(
		`${process.env.BACKEND_URL}/api/debates/${debateId}`,
		{
			cache: "no-cache"
		}
	);

	if (!response.ok) {
		if (response.status === 404) {
			console.debug(`Debate with ID ${debateId} not found.`);
			notFound();
		}

		const data = await response.json();
		console.error(`Error fetching debate with ID ${debateId}:`, data);

		throw new Error(
			`Failed to fetch debate with ID ${debateId}`,
		);
	}
	const data = await response.json();
	return data as DebateDetailsDTO;
}

export async function joinDebate(debateId: string): Promise<void> {

	const response = await backendFetch(`/api/debates/${debateId}/join`, {
		method: "POST",
	});

	if (!response.ok) {
		const data = await response.json();
		console.error(`Error joining debate with ID ${debateId}:`, data);
		throw new Error(
			`Failed to join debate with ID ${debateId}`,
		);
	}

	redirect(`/debates/${debateId}/live`);
}

export async function toggleReadyStatus(debateId: string): Promise<void> {
	const response = await backendFetch(`/api/debates/${debateId}/ready/toggle`, {
		method: "POST",
	});

	if (!response.ok) {
		const data = await response.json();
		console.error(`Error toggling ready status for debate with ID ${debateId}:`, data);
		throw new Error(
			`Failed to toggle ready status for debate with ID ${debateId}`,
		);
	}
	
}
