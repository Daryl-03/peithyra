import "server-only";

import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server";
import { redirect } from "next/navigation";
import { createSafeActionClient } from "next-safe-action";

const actionClient = createSafeActionClient();

export const authedClient = actionClient.use(async ({ next }) => {
    const { isAuthenticated } = getKindeServerSession();

    if (!(await isAuthenticated())) {
        redirect("/api/auth/login");
    }

    return next();
});
