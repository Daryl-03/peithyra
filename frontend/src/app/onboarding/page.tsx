
import type { Metadata } from "next";
import Link from "next/link";
import { redirect } from "next/navigation";
import { OnboardingForm } from "../../features/onboarding/components/onboarding-form";
import { backendFetch } from "@/lib/backend-fetch";

export const metadata: Metadata = { title: "Votre profil | Peithyra" };

export default async function OnboardingPage() {
	let profileState: "exists" | "missing" | "error" = "error";
	try {
		const response = await backendFetch(`/identity/me`, {
			cache: "no-store",
			signal: AbortSignal.timeout(10000),
		});
		if (response.ok) profileState = "exists";
		else if (response.status === 403) {
			const error = await response.json();
			if (error.code === "ONBOARDING_REQUIRED") profileState = "missing";
		}
	} catch {
		// An unavailable profile must not be treated as an absent profile.
	}
	if (profileState === "exists") redirect("/");

	return (
		<main className="flex w-full flex-1 items-center justify-center px-5 py-12 sm:px-8 sm:py-20">
			<section
				className="w-full max-w-md"
				aria-labelledby="onboarding-title"
			>
				<p className="mb-4 text-xs font-medium uppercase tracking-[0.18em] text-secondary-foreground">
					Bienvenue sur Peithyra
				</p>
				<h1
					id="onboarding-title"
					className="text-3xl font-medium tracking-tight sm:text-4xl"
				>
					Une voix. Votre pseudo.
				</h1>
				<p className="mt-4 leading-relaxed text-muted-foreground">
					Choisissez le nom qui accompagnera vos arguments. Il sera
					visible par les autres participants.
				</p>
				{profileState === "missing" ? (
					<OnboardingForm />
				) : (
					<div className="mt-8 rounded-lg border bg-card p-6">
						<p role="alert" className="text-sm">
							Impossible de charger votre profil pour le moment.
							Réessayez dans quelques instants.
						</p>
						<a
							href="/onboarding"
							className="mt-4 inline-block text-sm font-medium underline underline-offset-4"
						>
							Réessayer
						</a>
					</div>
				)}
				<div className="mt-8 text-center">
					<Link
						href="/"
						className="text-sm text-muted-foreground underline-offset-4 hover:text-foreground hover:underline"
					>
						Revenir aux débats
					</Link>
				</div>
			</section>
		</main>
	);
}
