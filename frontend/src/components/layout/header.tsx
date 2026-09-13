import { Button } from "@/components/ui/button";

export function Header() {
    return (
        <header className="w-full">
            <nav className="flex items-center justify-between p-8 border-b border-b-primary/10 py-4">
                <h1 className="text-2xl font-bold">Peithyra</h1>
                <div className="flex gap-4">
                    <Button variant="ghost" size="lg">
                        Sign In
                    </Button>
                    <Button variant="default" size="lg">
                        Create Account
                    </Button>
                </div>
            </nav>
        </header>
    );
}
