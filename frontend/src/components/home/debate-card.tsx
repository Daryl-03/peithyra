import { Dot, MoveRight } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

type DebateCardProps = {
    title: string;
    status: "looking" | "ongoing";
    forParticipant: string;
    againstParticipant?: string;
    currentRound?: number;
    totalRounds?: number;
};

export function DebateCard({
    title,
    status,
    forParticipant,
    againstParticipant,
    currentRound,
    totalRounds,
}: DebateCardProps) {
    return (
        <article className="flex flex-col gap-2 border-b border-b-primary/10 py-4">
            <p className="flex items-center text-muted-foreground uppercase">
                {status === "ongoing" ? (
                    <>
                        <Dot color="green" />
                        En cours - Tour {currentRound} / {totalRounds}
                    </>
                ) : (
                    "Cherche une opposition"
                )}
            </p>
            <h3 className="mt-2 mb-4 text-2xl leading-tight font-medium">
                {title}
            </h3>

            <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
                <div className="flex items-center gap-4">
                    <div className="flex gap-2">
                        <Badge variant="for">Pour</Badge>
                        <span>{forParticipant}</span>
                    </div>
                    <MoveRight />
                    <div className="flex gap-2">
                        <Badge variant="against">Contre</Badge>
                        <span>{againstParticipant ?? "???"}</span>
                    </div>
                </div>
                <Button variant="secondary" size="lg">
                    Voir la proposition
                    <MoveRight />
                </Button>
            </div>
        </article>
    );
}
