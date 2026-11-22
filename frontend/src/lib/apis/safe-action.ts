import "server-only";

import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server";
import { redirect } from "next/navigation";
import { createSafeActionClient } from "next-safe-action";
import { ApiError, DEFAULT_API_ERROR_MESSAGE } from "./api-error";

// Only messages explicitly intended for the user may cross the server boundary.
export class ActionError extends Error {}

export const actionClient = createSafeActionClient({
    handleServerError(error) {
        if (error instanceof ActionError) return error.message;
        const apiError = ApiError.fromUnknown(error);
        if (apiError.status < 500) {
            switch (apiError.code) {
                case "DEBATE_ALREADY_ONGOING":
                    return "You already have an ongoing debate. Please finish it before starting a new one.";
                case "PROFILE_CONFLICT":
                    return "Ce pseudo ou ce compte est d\u00e9j\u00e0 enregistr\u00e9. Essayez un autre pseudo ou revenez aux d\u00e9bats.";
            }
        }
        return DEFAULT_API_ERROR_MESSAGE;
    },
});

export const authedClient = actionClient.use(async ({ next }) => {
    const { isAuthenticated } = getKindeServerSession();

    if (!(await isAuthenticated())) {
        redirect("/api/auth/login");
    }

    return next();
});
