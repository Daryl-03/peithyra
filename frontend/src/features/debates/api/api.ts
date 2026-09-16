import { DebateSummary } from "../model/debate-summary";
import { DebateSummaryDTO } from "./debate-summary-dto";

export async function getLatestDebates(
    limit: number,
): Promise<DebateSummary[]> {
    try {
        const response = await fetch(
            `${process.env.BACKEND_URL}/api/debates?size=${limit}`,
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
