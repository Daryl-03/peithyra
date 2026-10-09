import Link from "next/link";

export default function AuthErrorPage() {
    return (
        <main className="flex flex-1 flex-col items-center justify-center gap-4 px-8 text-center">
            <h1 className="text-3xl font-medium">
                Le lien de connexion a expiré
            </h1>
            <p className="max-w-md text-muted-foreground">
                Recommencez la connexion depuis l’accueil et utilisez le nouveau
                lien reçu.
            </p>
            <Link
                href="/"
                className="text-primary underline underline-offset-4"
            >
                Retour à l’accueil
            </Link>
        </main>
    );
}
