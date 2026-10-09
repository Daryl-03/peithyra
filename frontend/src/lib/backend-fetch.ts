import "server-only";
import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server";
import { redirect } from "next/navigation";

export async function backendFetch(
    path: string,
    options: RequestInit = {},
    { redirectOnAuthError = true }: { redirectOnAuthError?: boolean } = {},
): Promise<Response> {
    const { getAccessTokenRaw } = getKindeServerSession();
    const token = await getAccessTokenRaw();
    if (!token) {
        if (redirectOnAuthError) redirect("/api/auth/login");
        return new Response(null, { status: 401 });
    }
    const url = `${process.env.BACKEND_URL}${path}`;
    const headers = new Headers(options.headers || {});
    headers.set("Authorization", `Bearer ${token}`);

    const response = await fetch(url, {
        cache: "no-store",
        ...options,
        headers,
    });
    if (redirectOnAuthError) {
        if (response.status === 401) redirect("/api/auth/login");
        if (response.status === 403) {
            const error = await response
                .clone()
                .json()
                .catch(() => null);
            if (error?.code === "ONBOARDING_REQUIRED") redirect("/onboarding");
        }
    }
    return response;
}
