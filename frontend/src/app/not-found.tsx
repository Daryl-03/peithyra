import Link from "next/link";
import { buttonVariants } from "@/components/ui/button";

export default function NotFound() {
	return (
		<div className="flex flex-col items-center gap-4 px-5 py-16 text-center">
			<p className="text-sm text-muted-foreground">404</p>
			<h1 className="text-2xl font-semibold">Page introuvable</h1>
			<p className="text-muted-foreground">
				La page demandée n’existe pas ou n’est plus disponible.
			</p>
			<Link href="/agora" className={buttonVariants()}>
				Retour à l’agora
			</Link>
		</div>
	);
}
