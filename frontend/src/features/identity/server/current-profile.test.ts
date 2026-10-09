import { afterEach, beforeEach, describe, expect, test, vi } from "vitest";
import { backendFetch } from "@/lib/backend-fetch";
import { getCurrentProfile, requireOnboardedUser } from "./current-profile";

vi.mock("@/lib/backend-fetch", () => ({ backendFetch: vi.fn() }));

const { redirectMock } = vi.hoisted(() => ({ redirectMock: vi.fn() }));
vi.mock("next/navigation", () => ({ redirect: redirectMock }));

const backendFetchMock = vi.mocked(backendFetch);
const profile = {
    id: "4c096978-0329-44ad-a9d3-730849304b88",
    username: "Camille",
};

beforeEach(() => {
    // Like Next.js, the mock interrupts execution instead of returning.
    redirectMock.mockImplementation((path: string) => {
        throw new Error(`redirect:${path}`);
    });
});

afterEach(() => vi.resetAllMocks());

describe("getCurrentProfile", () => {
    test("returns a validated profile and lets the caller handle authentication states", async () => {
        backendFetchMock.mockResolvedValue(Response.json(profile));

        expect(await getCurrentProfile()).toEqual({ status: "ready", profile });
        expect(backendFetchMock).toHaveBeenCalledExactlyOnceWith(
            "/identity/me",
            { cache: "no-store", signal: expect.any(AbortSignal) },
            { redirectOnAuthError: false },
        );
        expect(redirectMock).not.toHaveBeenCalled();
    });

    test("returns unauthorized for a 401 without requiring a JSON body", async () => {
        backendFetchMock.mockResolvedValue(new Response(null, { status: 401 }));

        expect(await getCurrentProfile()).toEqual({ status: "unauthorized" });
        expect(redirectMock).not.toHaveBeenCalled();
    });

    test("returns missing only for an explicit onboarding requirement", async () => {
        backendFetchMock.mockResolvedValue(
            Response.json({ code: "ONBOARDING_REQUIRED" }, { status: 403 }),
        );

        expect(await getCurrentProfile()).toEqual({ status: "missing" });
        expect(redirectMock).not.toHaveBeenCalled();
    });

    test.each([403, 404, 500, 503])(
        "does not treat HTTP %i as a missing profile",
        async (status) => {
            backendFetchMock.mockResolvedValue(
                Response.json({ code: "OTHER_ERROR" }, { status }),
            );

            expect(await getCurrentProfile()).toEqual({ status: "error" });
            expect(redirectMock).not.toHaveBeenCalled();
        },
    );

    test.each([
        { id: "not-a-uuid", username: "Camille" },
        { id: profile.id },
        { id: profile.id, username: null },
        null,
    ])("rejects an invalid profile despite HTTP 200: %j", async (body) => {
        backendFetchMock.mockResolvedValue(Response.json(body));

        expect(await getCurrentProfile()).toEqual({ status: "error" });
    });

    test.each([200, 403])(
        "handles malformed JSON with HTTP %i",
        async (status) => {
            backendFetchMock.mockResolvedValue(
                new Response("not JSON", { status }),
            );

            expect(await getCurrentProfile()).toEqual({ status: "error" });
        },
    );

    test("returns error when the backend is unreachable", async () => {
        backendFetchMock.mockRejectedValue(new TypeError("Failed to fetch"));

        expect(await getCurrentProfile()).toEqual({ status: "error" });
        expect(redirectMock).not.toHaveBeenCalled();
    });
});

describe("requireOnboardedUser", () => {
    test("returns the local profile when onboarding is complete", async () => {
        backendFetchMock.mockResolvedValue(Response.json(profile));

        expect(await requireOnboardedUser()).toEqual(profile);
        expect(backendFetchMock).toHaveBeenCalledTimes(1);
        expect(redirectMock).not.toHaveBeenCalled();
    });

    test("redirects an unauthenticated user to login", async () => {
        backendFetchMock.mockResolvedValue(new Response(null, { status: 401 }));

        await expect(requireOnboardedUser()).rejects.toThrow(
            "redirect:/api/auth/login",
        );
        expect(redirectMock).toHaveBeenCalledExactlyOnceWith("/api/auth/login");
    });

    test("redirects a user without a profile to onboarding", async () => {
        backendFetchMock.mockResolvedValue(
            Response.json({ code: "ONBOARDING_REQUIRED" }, { status: 403 }),
        );

        await expect(requireOnboardedUser()).rejects.toThrow(
            "redirect:/onboarding",
        );
        expect(redirectMock).toHaveBeenCalledExactlyOnceWith("/onboarding");
    });

    test("throws without redirecting when the backend is unavailable", async () => {
        backendFetchMock.mockRejectedValue(new TypeError("Failed to fetch"));

        await expect(requireOnboardedUser()).rejects.toThrow(
            "Unable to load the current profile",
        );
        expect(redirectMock).not.toHaveBeenCalled();
    });

    test("does not redirect unrelated forbidden responses to onboarding", async () => {
        backendFetchMock.mockResolvedValue(
            Response.json({ code: "FORBIDDEN" }, { status: 403 }),
        );

        await expect(requireOnboardedUser()).rejects.toThrow(
            "Unable to load the current profile",
        );
        expect(redirectMock).not.toHaveBeenCalled();
    });
});
