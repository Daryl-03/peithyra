"use client";

import { Button } from "@/components/ui/button";

export default function ErrorPage({
	reset,
}: {
	error: Error & { digest?: string };
	reset: () => void;
}) {
	return (
		<div className="flex flex-col items-center gap-4 px-5 py-16 text-center">
			<h1 className="text-2xl font-semibold">Une erreur est survenue</h1>
			<p role="alert" className="text-muted-foreground">
				Impossible de charger cette page. Veuillez réessayer.
			</p>
			<Button type="button" onClick={() => reset()}>
				Réessayer
			</Button>
		</div>
	);
}
