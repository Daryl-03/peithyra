import type { DebateStatus, ParticipantSummary } from "./debate-summary";

export type DebateDetails = {
    id: string;
    proposition: string;
    description: string;
    status: DebateStatus
    format: DebateFormat;
    createdAt: string;
    participants: ParticipantSummary[];
};

export type DebateFormat = {
    numberOfTurns: number;
    turnTimeLimitSeconds: number;
};
