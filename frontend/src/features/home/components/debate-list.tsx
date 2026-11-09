import {
    Empty,
    EmptyDescription,
    EmptyHeader,
    EmptyTitle,
} from "@/components/ui/empty";
import type { DebateSummary } from "@/features/debates/model/debate-summary";
import { DebateCard } from "./debate-card";

type DebateListProps = {
    debates: DebateSummary[];
    emptyTitle?: string;
};

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
                <DebateCard key={debate.id} debate={debate} />
            ))}
        </div>
    );
}
