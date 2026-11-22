import { unstable_rethrow } from "next/dist/client/components/unstable-rethrow.server";
import { redirect } from "next/navigation";
import { toggleReadyAction } from "@/features/debates/actions/toggle-ready";
import { getDebateById } from "@/features/debates/api/api";
import type { DebateDetailsDTO } from "@/features/debates/api/debate-details-dto";
import { DebateReadyPanel } from "@/features/debates/components/debate-ready-panel";
import { getCurrentProfile } from "@/features/identity/server/current-profile";

export default async function DebatePage({
    params,
}: {
    params: Promise<{ id: string }>;
}) {
    let debate: DebateDetailsDTO;
    const { id: debateId } = await params;
    const profileData = await getCurrentProfile();

    try {
        debate = await getDebateById(debateId);
        // if(debate.participants.length < 2) {debate.participants.push({
        // 	id: "placeholder",
        // 	username: "hey",
        // 	side: "FOR",
        // 	isReady: false,
        // } as ParticipantDetails);}
        if (debate.status === "WAITING_FOR_OPPONENT") {
            redirect(`debates/${debateId}`);
        }
        // if (profileData.status === "ready") {
        //     isUserInDebate = debate.participants.some(
        //         (participant) => participant.id === profileData?.profile.id,
        //     );
        // }
    } catch (error) {
        unstable_rethrow(error);
        return (
            <div className="flex items-center justify-center flex-1">
                <p role="alert" className="py-8 text-muted-foreground">
                    Impossible de charger le débat. Réessayez plus tard.
                </p>
            </div>
        );
    }

    const forParticipant = debate.participants.find(
        (participant) => participant.side === "FOR",
    );
    const againstParticipant = debate.participants.find(
        (participant) => participant.side === "AGAINST",
    );
    const currentParticipant =
        profileData.status === "ready"
            ? debate.participants.find(
                  (participant) => participant.id === profileData.profile.id,
              )
            : undefined;

    return (
        <main className="w-full space-y-10 flex-1 px-5 py-10 sm:px-0 sm:py-2">
            <section className="border-b pb-2 mx-auto">
                <div className="w-3xl flex justify-self-center flex-col gap-3 items-center">
                    <div className="pb-2 w-full">
                        <h1 className="text-2xl font-bold">
                            {debate.proposition}
                        </h1>
                    </div>

                    <div className="flex items-center w-full justify-between">
                        <div className="rounded-full flex items-center bg-camp-for gap-2 p-2">
                            {/* <div
								aria-hidden="true"
								className="flex  shrink-0 items-center justify-center rounded-full bg-secondary text-lg font-medium text-secondary-foreground"
							>
								{forParticipant?.username.slice(0, 2).toLocaleUpperCase("fr") || "P"}
							</div> */}
                            <span>
                                {forParticipant?.username ||
                                    "En attente d'un adversaire"}
                            </span>
                        </div>

                        <span className=" text-muted-foreground">VS</span>

                        <div className="rounded-full bg-camp-against p-2">
                            {againstParticipant?.username ||
                                "En attente d'un adversaire"}
                        </div>
                    </div>
                </div>
            </section>

            <DebateReadyPanel
                participants={debate.participants}
                currentParticipant={currentParticipant}
                toggleReadyAction={toggleReadyAction.bind(null, { debateId })}
            />
        </main>
    );
}
