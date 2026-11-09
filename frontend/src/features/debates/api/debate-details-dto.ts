import type { DebateStatus } from "./../model/debate-summary";
import type { ParticipantSummaryDTO } from "./debate-summary-dto";

export type DebateDetailsDTO = {
    id: string;
    proposition: string;
    description: string;
    status: DebateStatus;
    format: DebateFormatDTO;
    createdAt: string;
    participants: ParticipantSummaryDTO[];
};

export type DebateFormatDTO = {
    numberOfTurns: number;
    turnTimeLimitSeconds: number;
};
