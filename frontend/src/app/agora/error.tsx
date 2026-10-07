"use client";

import { Button } from "@/components/ui/button";

export default function AgoraError({ reset }: { reset: () => void }) {
    return (
        <main className="w-full flex-1 px-5 py-10 sm:px-8 sm:py-12">
            <h1 className="mb-6 text-3xl font-medium">Agora</h1>
            <div className="space-y-4 rounded-lg border bg-card p-6">
                <p role="alert">
                    Impossible de charger votre espace pour le moment.
                </p>
                <Button onClick={reset}>Réessayer</Button>
            </div>
        </main>
    );
}
