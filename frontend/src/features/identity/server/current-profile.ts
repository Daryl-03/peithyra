import "server-only";

import { redirect } from "next/navigation";
import { connection } from "next/server";
import { z } from "zod";
import { backendFetch } from "@/lib/backend-fetch";

const profileSchema = z.object({
    id: z.uuid(),
    username: z.string(),
});

type ProfileState =
    | { status: "ready"; profile: z.infer<typeof profileSchema> }
    | { status: "missing" | "unauthorized" | "error" };

export async function getCurrentProfile(): Promise<ProfileState> {
    try {
        // Onboarding must inspect a missing profile without redirecting to itself.
        const response = await backendFetch(
            "/identity/me",
            { cache: "no-store", signal: AbortSignal.timeout(10000) },
            { redirectOnAuthError: false },
        );
        if (response.status === 401) return { status: "unauthorized" };
        if (response.status === 403) {
            const error = await response.json();
            if (error.code === "ONBOARDING_REQUIRED")
                return { status: "missing" };
        }
        if (!response.ok) return { status: "error" };

        const profile = profileSchema.parse(await response.json());
        return { status: "ready", profile };
    } catch {
        return { status: "error" };
    }
}

export async function requireOnboardedUser() {
    await connection();

    const result = await getCurrentProfile();
    if (result.status === "unauthorized") redirect("/api/auth/login");
    if (result.status === "missing") redirect("/onboarding");
    if (result.status === "ready") return result.profile;

    throw new Error("Unable to load the current profile");
}
