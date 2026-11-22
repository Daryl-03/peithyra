"use client";

import { useAction } from "next-safe-action/hooks";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { joinDebate } from "../actions/join-debate";
import { leaveDebate } from "../actions/leave-debate";
import type { DebateStatus } from "@/features/debates/model/debate-summary";
import Link from "next/link";

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
            toast.error(
                error.serverError ??
                    "Impossible de rejoindre le débat. Réessayez.",
            );
        },
    });

    const { execute: executeLeave, isPending: isLeaving } = useAction(
        leaveDebate,
        {
            onError: ({ error }) => {
                toast.error(
                    error.serverError ??
                        "Impossible de quitter le débat. Réessayez.",
                );
            },
        },
    );

    if (status === "WAITING_FOR_OPPONENT") {
        if (isParticipant) {
            return (
                <div className="flex flex-col gap-2">
                    <h2 className="text-lg font-semibold">
                        En attente d'un adversaire.
                    </h2>
                    <p className="text-muted-foreground text-sm">
                        Dès qu’une personne rejoint l'autre camp, le débat
                        commence. Vous serez automatiquement redirigé vers le
                        débat.{" "}
                        <span className="font-semibold text-foreground">
                            Gardez cette page ouverte.
                        </span>
                    </p>
                    <Button
                        type="button"
                        variant="default"
                        size="lg"
                        className="w-full"
                        disabled={isLeaving}
                        onClick={() => executeLeave({ debateId })}
                    >
                        {isLeaving ? "Départ en cours…" : "Quitter le débat"}
                    </Button>
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
                        if (availableSide)
                            execute({ debateId, side: availableSide });
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

    if (status === "WAITING_FOR_READY" && isParticipant) {
        return (
            <div className="flex flex-col gap-2">
                <h2 className="text-lg font-semibold">
                    Le débat est prêt à commencer.
                </h2>
                <p className="text-muted-foreground text-sm">
                    Vous êtes prêt à participer au débat. Cliquez{" "}
                    <Link href={`/debates/${debateId}/live`}>
                        <span className="text-primary hover:underline font-semibold">
                            ici
                        </span>
                    </Link>{" "}
                    pour commencer.
                </p>
                <Button
                    type="button"
                    variant="default"
                    size="lg"
                    className="w-full"
                    disabled={isLeaving}
                    onClick={() => executeLeave({ debateId })}
                >
                    {isLeaving ? "Départ en cours…" : "Quitter le débat"}
                </Button>
            </div>
        );
    }

    return null;
}
