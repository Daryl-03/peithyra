export type DebateSummary = {
    id: string;
    proposition: string;
    status: DebateStatus;
    createdAt: string;
    participants: ParticipantSummary[];
};

export type ParticipantSummary = {
    id: string;
    username: string;
    side: DebateSide;
};

export type DebateSide = "FOR" | "AGAINST";

export type DebateStatus = "WAITING_FOR_OPPONENT" | "DONE" | "CANCELED" | "ONGOING";