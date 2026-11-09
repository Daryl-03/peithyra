import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server";
import { DebateSide } from "@/features/agora/models/debate-side";
import { getDebateById } from "@/features/debates/api/api";
import type { DebateDetailsDTO } from "@/features/debates/api/debate-details-dto";
import { DebateParticipation } from "@/features/debates/components/debate-participation";
import { getCurrentProfile } from "@/features/identity/server/current-profile";

export default async function DebatePage({
    params,
}: {
    params: Promise<{ id: string }>;
}) {
    let debate: DebateDetailsDTO;
    const { id: debateId } = await params;
    const { isAuthenticated } = await getKindeServerSession();
    const authenticated = await isAuthenticated();
    const profile = await getCurrentProfile();
    let isUserInDebate = false;

    try {
        debate = await getDebateById(debateId);
        if (profile.status === "ready") {
            isUserInDebate = debate.participants.some(
                (participant) => participant.id === profile?.profile.id,
            );
        }
    } catch {
        return (
            <div className="flex items-center justify-center flex-1">
                <p role="alert" className="py-8 text-muted-foreground">
                    Impossible de charger le débat. Réessayez plus tard.
                </p>
            </div>
        );
    }

    return (
        <main className="w-full space-y-10 flex-1 px-5 py-10 sm:px-8 sm:py-12">
            <section className="flex flex-col gap-10">
                <p className="text-secondary-foreground">
                    {" "}
                    {debate.status === "WAITING_FOR_OPPONENT" &&
                        "En attente d'un adversaire."}{" "}
                </p>
                <h1 className="text-3xl sm:text-5xl font-bold sm:w-[70%]">
                    {debate.proposition}
                </h1>
                <p className="text-muted-foreground">{debate.description}</p>
            </section>
            <hr />
            <section className=" grid gap-8 lg:grid-cols-[minmax(0,3fr)_minmax(0,2fr)] justify-between py-6 ">
                <div className="flex flex-col gap-10">
                    <div className="flex  justify-between">
                        <div className="flex bg-camp-for p-6 w-[45%] flex-col gap-3 rounded-lg">
                            <h2 className="text-md text-secondary-foreground font-semibold">
                                Pour
                            </h2>
                            <p className=" text-2xl">
                                {debate.participants.find(
                                    (participant) =>
                                        participant.side === DebateSide.For,
                                )?.username || "Place disponible"}
                            </p>
                        </div>
                        <div className="flex bg-camp-against p-6 flex-col  w-[45%] gap-3 rounded-lg">
                            <h2 className="text-md text-secondary-foreground font-semibold">
                                Contre
                            </h2>
                            <p className="text-2xl">
                                {debate.participants.find(
                                    (participant) =>
                                        participant.side === DebateSide.Against,
                                )?.username || "Place disponible"}
                            </p>
                        </div>
                    </div>

                    <div className="bg-camp-for p-4 flex flex-col gap-4 rounded-lg">
                        <h2 className="text-secondary-foreground font-semibold">
                            On débat des idées, pas des personnes.
                        </h2>
                        <p>
                            Les débats sont un espace pour échanger des idées et
                            des perspectives. Veuillez vous abstenir de tout
                            langage offensant, discriminatoire ou personnel.
                        </p>
                    </div>
                </div>

                <div className="bg-card p-6  space-y-6 rounded-lg">
                    <h2 className="text-lg font-semibold ">
                        Le cadre du débat
                    </h2>

                    <div className="flex flex-col gap-2 text-sm">
                        <div className="text-muted-foreground border-b py-2 grid grid-cols-2 gap-4 items-center">
                            <span className="font-semibold uppercase">
                                Format
                            </span>

                            <span className="">Texte, en alternance</span>
                        </div>
                        <div className="text-muted-foreground border-b py-2 grid grid-cols-2 gap-4 items-center">
                            <span className="font-semibold uppercase">
                                Nombre de tours
                            </span>
                            <span className="">
                                {" "}
                                {debate.format.numberOfTurns}
                            </span>
                        </div>
                        <div className="text-muted-foreground border-b py-2 grid grid-cols-2 gap-4 items-center">
                            <span className="font-semibold uppercase">
                                Temps par tour
                            </span>
                            <span className="">
                                {" "}
                                {debate.format.turnTimeLimitSeconds} secondes
                            </span>
                        </div>
                    </div>

                    <DebateParticipation
                        status={debate.status}
                        isParticipant={isUserInDebate}
                        authenticated={authenticated}
                    />
                </div>
            </section>
        </main>
    );
}
