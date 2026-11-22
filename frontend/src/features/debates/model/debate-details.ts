import type { DebateStatus } from "./debate-summary";

export type DebateDetails = {
    id: string;
    proposition: string;
    description: string;
    status: DebateStatus;
    format: DebateFormat;
    createdAt: string;
    participants: ParticipantDetails[];
};

export type ParticipantDetails = {
    id: string;
    username: string;
    side: "FOR" | "AGAINST";
    isReady: boolean;
};

export type DebateFormat = {
    numberOfTurns: number;
    turnTimeLimitSeconds: number;
};
