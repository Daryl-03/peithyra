import "server-only";

import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server";
import { redirect } from "next/navigation";
import { createSafeActionClient } from "next-safe-action";

// Only messages explicitly intended for the user may cross the server boundary.
export class ActionError extends Error {}

const actionClient = createSafeActionClient({
    handleServerError(error) {
        if (error instanceof ActionError) return error.message;
        console.error("Server action failed:", error);
        return "Le service est momentanément indisponible. Veuillez réessayer.";
    },
});

export const authedClient = actionClient.use(async ({ next }) => {
    const { isAuthenticated } = getKindeServerSession();

    if (!(await isAuthenticated())) {
        redirect("/api/auth/login");
    }

    return next();
});
