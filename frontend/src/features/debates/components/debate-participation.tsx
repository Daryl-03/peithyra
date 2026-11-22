"use client";

import { useAction } from "next-safe-action/hooks";
import { Button } from "@/components/ui/button";
import { toast } from "@/components/ui/toast";
import { joinDebate } from "../actions/join-debate";
import type { DebateStatus } from "@/features/debates/model/debate-summary";

type DebateParticipationProps = {
    debateId: string;
    availableSide: "FOR" | "AGAINST" | null;
    status: DebateStatus;
    isParticipant: boolean;
    authenticated: boolean | null;
};

export function DebateParticipation({
    debateId,
    availableSide,
    status,
    isParticipant,
    authenticated,
}: DebateParticipationProps) {
    const { execute, isPending } = useAction(joinDebate, {
        onError: ({ error }) => {
            toast.add({
                type: "error",
                title: error.serverError ?? "Impossible de rejoindre le débat. Réessayez.",
            });
        },
    });

    if (status === "WAITING_FOR_OPPONENT") {
        if (isParticipant) {
            return (
                <div>
                    <h2 className="text-lg font-semibold">
                        En attente d'un adversaire.
                    </h2>
                    <p className="text-muted-foreground text-sm">
                        Dès qu’une personne rejoint l'autre camp, le débat
                        commence. Vous serez automatiquement redirigé vers le
                        débat. <span className="font-semibold text-foreground">Gardez cette page ouverte.</span>
                    </p>
                </div>
            );
        }

        return (
            <>
                <div>
                    <h2 className="text-lg font-semibold">
                        La place est à vous !
                    </h2>
                    <p className="text-muted-foreground text-sm">
                        Venez débattre avec nous et partagez vos idées sur ce
                        sujet passionnant. Que vous soyez pour ou contre, votre
                        voix compte !
                    </p>
                </div>
                <Button
                    type="button"
                    variant="default"
                    size="lg"
                    className="w-full"
                    disabled={isPending || !availableSide}
                    onClick={() => {
                        if (availableSide) execute({ debateId, side: availableSide });
                    }}
                >
                    {isPending ? "Connexion au débat…" : "Rejoindre le débat"}
                </Button>
                {!authenticated && (
                    <p className="text-muted-foreground text-sm">
                        Vous devez être connecté pour rejoindre le débat.
                    </p>
                )}
            </>
        );
    }

    if (status === "ONGOING" && !isParticipant) {
        return (
            <Button variant="default" size="lg" className="w-full">
                Assister
            </Button>
        );
    }

    return null;
}
