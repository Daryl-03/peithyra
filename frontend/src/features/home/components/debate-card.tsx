import { Dot, MoveRight } from "lucide-react";
import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import type { DebateSummary } from "@/features/debates/model/debate-summary";

type DebateCardProps = {
    debate: DebateSummary;
};

export function DebateCard({ debate }: DebateCardProps) {
    const forParticipant = debate.participants.find(
        (participant) => participant.side === "FOR",
    )?.username;
    const againstParticipant = debate.participants.find(
        (participant) => participant.side === "AGAINST",
    )?.username;
    return (
        <article className="flex flex-col gap-2 border-b border-b-primary/10 py-4">
            <p className="flex items-center text-muted-foreground uppercase">
                {debate.status === "ONGOING" ? (
                    <>
                        <Dot color="green" />
                        En cours
                    </>
                ) : debate.status === "DONE" ? (
                    "Terminé"
                ) : debate.status === "CANCELED" ? (
                    "Annulé"
                ) : (
                    "Cherche une opposition"
                )}
            </p>
            <h3 className="mt-2 mb-4 text-2xl leading-tight font-medium">
                {debate.proposition}
            </h3>

            <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
                <div className="flex items-center gap-4">
                    <div className="flex gap-2">
                        <Badge variant="for">Pour</Badge>
                        <span>{forParticipant ?? "Place disponible"}</span>
                    </div>
                    <MoveRight />
                    <div className="flex gap-2">
                        <Badge variant="against">Contre</Badge>
                        <span>{againstParticipant ?? "Place disponible"}</span>
                    </div>
                </div>
                <Link href={`/debates/${debate.id}`}>
                    <Button variant="secondary" size="lg">
                        Voir la proposition
                        <MoveRight />
                    </Button>
                </Link>
            </div>
        </article>
    );
}
