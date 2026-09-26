import { Button } from "@/components/ui/button";
import {
    RegisterLink,
    LoginLink,
} from "@kinde-oss/kinde-auth-nextjs/components";
import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server"

export function Header() {
    const { isAuthenticated } = getKindeServerSession();

    return (
        <header className="w-full">
            <nav className="flex items-center justify-between p-8 border-b border-b-primary/10 py-4">
                <span className="text-xl sm:text-2xl font-bold">Peithyra</span>
                <div className="flex gap-1 sm:gap-4">
                    <LoginLink>
                        <Button variant="ghost" size="lg">
                            Sign In
                        </Button>
                    </LoginLink>
                    <RegisterLink className="">
                        <Button variant="default" size="lg">
                            Get Started
                        </Button>
                    </RegisterLink>
                </div>
            </nav>
        </header>
    );
}
