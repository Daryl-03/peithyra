import { backendFetch } from "@/lib/apis/backend-fetch";
import type { DebateSummary } from "../model/debate-summary";
import type { DebateDetailsDTO } from "./debate-details-dto";
import type { DebateSummaryDTO } from "./debate-summary-dto";

export async function getLatestDebates(
    limit: number,
): Promise<DebateSummary[]> {
    try {
        const response = await backendFetch(
            `/api/debates?size=${limit}`,
        );
		
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
	try {
		const response = await backendFetch(
			`/api/debates/${debateId}`,
		);
		
		if (!response.ok) {
			
			throw new Error(
				`Failed to fetch debate with ID ${debateId}: ${response.statusText}`,
			);
		}
		const data = await response.json();
		return data as DebateDetailsDTO;

	} catch (error) {
		console.error(`Error fetching debate with ID ${debateId}:`, error);
		throw error;
	}
}