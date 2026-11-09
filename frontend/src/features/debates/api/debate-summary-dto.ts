import type { DebateStatus } from "./../model/debate-summary";

export type DebateSummaryDTO = {
    id: string;
    proposition: string;
    status: DebateStatus;
    createdAt: string;
    participantViewList: ParticipantSummaryDTO[];
};

export type ParticipantSummaryDTO = {
    id: string;
    username: string;
    side: "FOR" | "AGAINST";
};
