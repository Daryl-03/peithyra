import type { DebateStatus } from "./../model/debate-summary";

export type DebateDetailsDTO = {
    id: string;
    proposition: string;
    description: string;
    status: DebateStatus;
    format: DebateFormatDTO;
    createdAt: string;
    creatorId: string;
    participants: ParticipantDetailsDTO[];
};

export type ParticipantDetailsDTO = {
    id: string;
    username: string;
    side: "FOR" | "AGAINST";
    isReady: boolean;
};

export type DebateFormatDTO = {
    numberOfTurns: number;
    turnTimeLimitSeconds: number;
};
