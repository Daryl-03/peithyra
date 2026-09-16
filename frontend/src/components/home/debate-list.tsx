import { DebateCard } from "@/components/home/debate-card";
import {
    Empty,
    EmptyDescription,
    EmptyHeader,
    EmptyTitle,
} from "@/components/ui/empty";
import type { DebateSummary } from "@/features/debates/model/debate-summary";

type DebateListProps = {
    debates: DebateSummary[];
    emptyTitle?: string;
};

const cardStatus = {
    WAITING_FOR_OPPONENT: "looking",
    ONGOING: "ongoing",
    DONE: "done",
    CANCELED: "canceled",
} as const;

export function DebateList({
    debates,
    emptyTitle = "Aucune proposition disponible",
}: DebateListProps) {
    if (debates.length === 0) {
        return (
            <Empty>
                <EmptyHeader>
                    <EmptyTitle>{emptyTitle}</EmptyTitle>
                    <EmptyDescription>
                        Revenez plus tard ou lancez une proposition.
                    </EmptyDescription>
                </EmptyHeader>
            </Empty>
        );
    }

    return (
        <div className="flex flex-col gap-4">
            {debates.map((debate) => (
                <DebateCard
                    key={debate.id}
                    title={debate.proposition}
                    status={cardStatus[debate.status]}
                    forParticipant={
                        debate.participants.find(
                            (participant) => participant.side === "FOR",
                        )?.username
                    }
                    againstParticipant={
                        debate.participants.find(
                            (participant) => participant.side === "AGAINST",
                        )?.username
                    }
                />
            ))}
        </div>
    );
}
