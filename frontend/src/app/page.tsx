import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server";
import { MoveRight } from "lucide-react";
import { redirect } from "next/navigation";
import { connection } from "next/server";
import { Suspense } from "react";
import { LatestDebates } from "@/components/home/latest-debates";
import { Button } from "@/components/ui/button";

export default async function Home() {
    await connection();

    const { isAuthenticated } = getKindeServerSession();

    if (await isAuthenticated()) {
        redirect("/agora");
    }

    return (
        <main className="flex-1 py-10 px-8 w-full flex flex-col gap-16">
            <section className="flex flex-col gap-2 w-full lg:w-1/2 items-start">
                <p className="uppercase text-muted-foreground  ">
                    Une proposition. Deux camps.
                </p>
                <h1 className="text-5xl font-medium leading-tight mt-2 mb-4">
                    Vos convictions méritent une vraie confrontation.
                </h1>
                <p className="text-muted-foreground  ">
                    Défendez ou contestez une proposition, à deux et à tour de
                    rôle. Ou lisez les échanges pour vous faire votre propre
                    opinion.
                </p>

                <Button variant="default" size="lg" className="mt-4">
                    Lancer une proposition
                    <MoveRight />
                </Button>
            </section>

            <section>
                <h2 className="text-3xl font-medium leading-tight mt-2 mb-4">
                    Les propositions du moment
                </h2>

                <Suspense fallback={<output>Chargement des débats…</output>}>
                    <LatestDebates />
                </Suspense>
            </section>
        </main>
    );
}
