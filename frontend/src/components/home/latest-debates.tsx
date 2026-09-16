import { DebateList } from "@/components/home/debate-list";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { getLatestDebates } from "@/features/debates/api/api";
import type { DebateSummary } from "@/features/debates/model/debate-summary";

export async function LatestDebates() {
    let debates: DebateSummary[];
    try {
        debates = await getLatestDebates(5);
    } catch {
        return (
            <p role="alert" className="py-8 text-muted-foreground">
                Impossible de charger les propositions. Réessayez plus tard.
            </p>
        );
    }

    return (
        <Tabs defaultValue="tous">
            <div className="w-full border-b border-b-primary/10">
                <TabsList variant="line" className="lg:gap-10">
                    <TabsTrigger value="tous">Tous</TabsTrigger>
                    <TabsTrigger value="join">À rejoindre</TabsTrigger>
                    <TabsTrigger value="ongoing">En cours</TabsTrigger>
                </TabsList>
            </div>
            <TabsContent value="tous">
                <DebateList debates={debates} />
            </TabsContent>
            <TabsContent value="join">
                <DebateList
                    debates={debates.filter(
                        (debate) => debate.status === "WAITING_FOR_OPPONENT",
                    )}
                    emptyTitle="Aucune proposition à rejoindre dans cette sélection"
                />
            </TabsContent>
            <TabsContent value="ongoing">
                <DebateList
                    debates={debates.filter(
                        (debate) => debate.status === "ONGOING",
                    )}
                    emptyTitle="Aucun débat en cours dans cette sélection"
                />
            </TabsContent>
        </Tabs>
    );
}
