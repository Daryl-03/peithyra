export type DebateSummary = {
    id: string;
    proposition: string;
    status: "WAITING_FOR_OPPONENT" | "DONE" | "CANCELED" | "ONGOING";
    createdAt: string;
    participants: ParticipantSummary[];
};

export type ParticipantSummary = {
    id: string;
    username: string;
    side: DebateSide;
};

export type DebateSide = "FOR" | "AGAINST";
