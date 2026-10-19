import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { afterEach, beforeEach, describe, expect, test, vi } from "vitest";
import { backendFetch } from "@/lib/apis/backend-fetch";
import { createProfile } from "./create-profile";

vi.mock("@/lib/apis/backend-fetch", () => ({ backendFetch: vi.fn() }));
vi.mock("next/cache", () => ({ revalidatePath: vi.fn() }));
vi.mock("@kinde-oss/kinde-auth-nextjs/server", () => ({
    getKindeServerSession: () => ({ isAuthenticated: async () => true }),
}));
vi.mock("next/navigation", async (importOriginal) => {
    const actual = await importOriginal<typeof import("next/navigation")>();
    return { ...actual, redirect: vi.fn(actual.redirect) };
});

const fetchMock = vi.mocked(backendFetch);
const genericError =
    "Le service est momentanément indisponible. Veuillez réessayer.";

beforeEach(async () => {
    // Preserve Next.js's real redirect exception, recognized by next-safe-action.
    const actual =
        await vi.importActual<typeof import("next/navigation")>(
            "next/navigation",
        );
    vi.mocked(redirect).mockImplementation(actual.redirect);
    vi.spyOn(console, "error").mockImplementation(() => {});
});
afterEach(() => {
    vi.resetAllMocks();
    vi.restoreAllMocks();
});

describe("createProfile", () => {
    test("posts the trimmed username, revalidates the layout and redirects on success", async () => {
        fetchMock.mockResolvedValue(new Response(null, { status: 201 }));
        await expect(
            createProfile({ username: "  Camille  " }),
        ).rejects.toThrow("NEXT_REDIRECT");

        expect(fetchMock).toHaveBeenCalledExactlyOnceWith(
            "/identity/register",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: expect.any(URLSearchParams),
                cache: "no-store",
                signal: expect.any(AbortSignal),
            },
        );
        const body = fetchMock.mock.calls[0][1]?.body as URLSearchParams;
        expect(body.toString()).toBe("username=Camille");
        expect(revalidatePath).toHaveBeenCalledExactlyOnceWith("/", "layout");
        expect(redirect).toHaveBeenCalledExactlyOnceWith("/agora");
        expect(
            vi.mocked(revalidatePath).mock.invocationCallOrder[0],
        ).toBeLessThan(vi.mocked(redirect).mock.invocationCallOrder[0]);
    });

    test("rejects invalid input before calling the backend", async () => {
        const result = await createProfile({ username: "abc" });
        expect(
            result?.validationErrors?.username?._errors?.length,
        ).toBeGreaterThan(0);
        expect(fetchMock).not.toHaveBeenCalled();
        expect(revalidatePath).not.toHaveBeenCalled();
        expect(redirect).not.toHaveBeenCalled();
    });

    test("exposes the intended conflict message without redirecting", async () => {
        fetchMock.mockResolvedValue(new Response(null, { status: 409 }));
        expect(await createProfile({ username: "Camille" })).toEqual({
            serverError:
                "Ce pseudo ou ce compte est déjà enregistré. Essayez un autre pseudo ou revenez aux débats.",
        });
        expect(revalidatePath).not.toHaveBeenCalled();
        expect(redirect).not.toHaveBeenCalled();
    });

    test.each([
        400, 401, 403, 404, 500,
    ])("masks HTTP %i without revalidation or redirect", async (status) => {
        fetchMock.mockResolvedValue(
            new Response("Internal backend details", { status }),
        );
        expect(await createProfile({ username: "Camille" })).toEqual({
            serverError: genericError,
        });
        expect(revalidatePath).not.toHaveBeenCalled();
        expect(redirect).not.toHaveBeenCalled();
    });

    test("masks network failures without redirecting", async () => {
        fetchMock.mockRejectedValue(new TypeError("Internal network details"));
        expect(await createProfile({ username: "Camille" })).toEqual({
            serverError: genericError,
        });
        expect(revalidatePath).not.toHaveBeenCalled();
        expect(redirect).not.toHaveBeenCalled();
    });
});
