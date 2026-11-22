"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Clock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import type { ParticipantDetailsDTO } from "../api/debate-details-dto";
import type { ToggleReadyActionResult } from "../actions/toggle-ready";

type DebateReadyPanelProps = {
    participants: ParticipantDetailsDTO[];
    currentParticipant?: ParticipantDetailsDTO;
    toggleReadyAction: () => Promise<ToggleReadyActionResult>;
};

export function DebateReadyPanel({
    participants,
    currentParticipant,
    toggleReadyAction,
}: DebateReadyPanelProps) {
    const router = useRouter();
    const [isPending, setIsPending] = useState(false);

    async function handleReadyToggle() {
        if (!currentParticipant || isPending) return;
        setIsPending(true);
        try {
            const result = await toggleReadyAction();
            if (result?.serverError) {
                toast.error(result.serverError);
                return;
            }
            if (result?.validationErrors || !result?.data?.success) {
                toast.error("Impossible de modifier votre état. Réessayez.");
                return;
            }
            toast.success("Vous êtes maintenant prêt.");
            router.refresh();
        } catch {
            toast.error("Impossible de modifier votre état. Réessayez.");
        } finally {
            setIsPending(false);
        }
    }

    return (
        <section className="mx-auto w-3xl">
            <div className="flex bg-card p-8 shadow-md rounded-md flex-col gap-3 items-center">
                <Clock />
                <p className="text-sm text-muted-foreground">
                    Le débat commencera dès que les deux participants seront
                    prêts.
                </p>
                <div className="flex w-full flex-col gap-3 sm:flex-row sm:justify-center sm:gap-6">
                    {participants.map((participant) => (
                        <div
                            key={participant.id}
                            className="flex items-center gap-2 rounded-md border px-4 py-3 text-sm"
                        >
                            <span
                                aria-hidden="true"
                                className={`size-2 shrink-0 rounded-full ${participant.isReady ? "bg-green-500" : "bg-muted-foreground"}`}
                            />
                            <span className="font-medium">
                                {participant.username}
                            </span>
                            <span className="text-muted-foreground">
                                {participant.isReady ? "Prêt" : "Pas prêt"}
                            </span>
                        </div>
                    ))}
                </div>
                {currentParticipant && (
                    <Button
                        onClick={handleReadyToggle}
                        disabled={isPending}
                        type="button"
                        variant={
                            currentParticipant.isReady ? "outline" : "default"
                        }
                        aria-pressed={currentParticipant.isReady}
                        className="mt-2"
                    >
                        {isPending
                            ? "Mise à jour…"
                            : currentParticipant.isReady
                              ? "Je ne suis plus prêt"
                              : "Je suis prêt"}
                    </Button>
                )}
            </div>
        </section>
    );
}
