import { handleAuth } from "@kinde-oss/kinde-auth-nextjs/server";
import { type NextRequest, NextResponse } from "next/server";

const kindeHandler = handleAuth();

export async function GET(
    request: NextRequest,
    context: { params: Promise<{ kindeAuth: string }> },
) {
    const response = await kindeHandler(request, context);
    const { kindeAuth } = await context.params;

    if (kindeAuth === "kinde_callback" && response.status >= 400) {
        console.error("Échec du callback Kinde, statut :", response.status);

        return NextResponse.redirect(new URL("/auth/error", request.url));
    }

    return response;
}
