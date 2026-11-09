import { Button } from "@/components/ui/button";
import type { DebateStatus } from "@/features/debates/model/debate-summary";

type DebateParticipationProps = {
	status: DebateStatus;
	isParticipant: boolean;
	authenticated: boolean | null;
};

export function DebateParticipation({
	status,
	isParticipant,
	authenticated,
}: DebateParticipationProps) {
	if (status === "WAITING_FOR_OPPONENT") {
		if (isParticipant) {
			return (
				<div>
					<h2 className="text-lg font-semibold">
						En attente d'un adversaire.
					</h2>
					<p className="text-muted-foreground text-sm">
						Dès qu’une personne rejoint l'autre camp, le débat commence.
						Vous serez automatiquement redirigé vers le débat. Gardez
						cette page ouverte.
					</p>
				</div>
			);
		}

		return (
			<>
				<div>
					<h2 className="text-lg font-semibold">La place est à vous !</h2>
					<p className="text-muted-foreground text-sm">
						Venez débattre avec nous et partagez vos idées sur ce sujet
						passionnant. Que vous soyez pour ou contre, votre voix compte !
					</p>
				</div>
				<Button variant="default" size="lg" className="w-full">
					Rejoindre le débat
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
