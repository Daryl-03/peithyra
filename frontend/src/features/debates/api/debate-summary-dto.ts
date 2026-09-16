export type DebateSummaryDTO = {
    id: string;
    proposition: string;
    status: "WAITING_FOR_OPPONENT" | "DONE" | "CANCELED" | "ONGOING";
    createdAt: string;
    participantViewList: ParticipantSummaryDTO[];
};

export type ParticipantSummaryDTO = {
    id: string;
    username: string;
    side: "FOR" | "AGAINST";
};
