import type { Metadata } from "next";

import { Suspense } from "react";
import { LatestDebates } from "@/components/home/latest-debates";
import { requireOnboardedUser } from "@/features/identity/server/current-profile";

export const metadata: Metadata = { title: "Agora | Peithyra" };

export default async function AgoraPage() {
    await requireOnboardedUser();

    return (
        <main className="w-full flex-1 px-5 py-10 sm:px-8 sm:py-12">
            <div className="mb-8 space-y-3">
                <h1 className="text-3xl font-medium tracking-tight sm:text-4xl">
                    Agora
                </h1>
                <p className="text-muted-foreground">
                    Une proposition à défendre, un point de vue à découvrir.
                </p>
            </div>

            <section aria-labelledby="debates-title">
                <h2 id="debates-title" className="mb-5 text-xl font-medium">
                    Les propositions du moment
                </h2>
                <Suspense
                    fallback={
                        <output className="block py-8 text-muted-foreground">
                            Chargement des débats…
                        </output>
                    }
                >
                    <LatestDebates />
                </Suspense>
            </section>
        </main>
    );
}
