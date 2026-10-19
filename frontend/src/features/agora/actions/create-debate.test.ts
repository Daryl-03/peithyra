import { revalidatePath } from "next/cache";
import { afterEach, beforeEach, describe, expect, test, vi } from "vitest";
import { backendFetch } from "@/lib/apis/backend-fetch";
import { DebateSide } from "../models/debate-side";
import { createDebate } from "./create-debate";

vi.mock("@/lib/apis/backend-fetch", () => ({ backendFetch: vi.fn() }));
vi.mock("next/cache", () => ({ revalidatePath: vi.fn() }));
vi.mock("@kinde-oss/kinde-auth-nextjs/server", () => ({
    getKindeServerSession: () => ({ isAuthenticated: async () => true }),
}));

const fetchMock = vi.mocked(backendFetch);
const input = {
    proposition: "Une proposition à débattre",
    description: "Une description suffisamment longue.",
    side: DebateSide.For,
    numberOfTurns: 3,
    turnTimeLimitSeconds: 180,
};
const genericError =
    "Le service est momentanément indisponible. Veuillez réessayer.";

beforeEach(() => {
    vi.spyOn(console, "error").mockImplementation(() => {});
});
afterEach(() => {
    vi.resetAllMocks();
    vi.restoreAllMocks();
});

describe("createDebate", () => {
    test("posts validated JSON, returns the debate and revalidates Agora", async () => {
        const debate = { id: "debate-1", ...input };
        fetchMock.mockResolvedValue(Response.json(debate));

        const result = await createDebate({
            ...input,
            proposition: `  ${input.proposition}  `,
        });

        expect(fetchMock).toHaveBeenCalledExactlyOnceWith("/api/debates", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(input),
        });
        expect(result).toEqual({ data: debate });
        expect(revalidatePath).toHaveBeenCalledExactlyOnceWith("/agora");
    });

    test("rejects invalid input before calling the backend", async () => {
        const result = await createDebate({ ...input, numberOfTurns: 0 });
        expect(
            result?.validationErrors?.numberOfTurns?._errors?.length,
        ).toBeGreaterThan(0);
        expect(fetchMock).not.toHaveBeenCalled();
        expect(revalidatePath).not.toHaveBeenCalled();
    });

    test.each([
        400, 401, 403, 404, 409, 500,
    ])("masks HTTP %i and does not revalidate", async (status) => {
        fetchMock.mockResolvedValue(
            new Response("Internal backend details", { status }),
        );
        expect(await createDebate(input)).toEqual({
            serverError: genericError,
        });
        expect(revalidatePath).not.toHaveBeenCalled();
    });

    test("masks network failures without revalidation", async () => {
        fetchMock.mockRejectedValue(new TypeError("Internal network details"));
        expect(await createDebate(input)).toEqual({
            serverError: genericError,
        });
        expect(revalidatePath).not.toHaveBeenCalled();
    });

    test("does not revalidate if the success response contains malformed JSON", async () => {
        fetchMock.mockResolvedValue(new Response("not JSON", { status: 200 }));
        expect(await createDebate(input)).toEqual({
            serverError: genericError,
        });
        expect(revalidatePath).not.toHaveBeenCalled();
    });
});
