import { afterEach, beforeEach, expect, test, vi } from "vitest";
import { backendFetch } from "./backend-fetch";

const { fetchMock } = vi.hoisted(() => ({
    fetchMock: vi.fn<typeof fetch>(),
}));

beforeEach(() => {
    vi.stubGlobal("fetch", fetchMock);
    vi.stubEnv("BACKEND_URL", "http://localhost:8080");
    // Next.js redirects interrupt execution rather than returning a response.
    redirectMock.mockImplementation((path: string) => {
        throw new Error(`redirect:${path}`);
    });
});

afterEach(() => {
    vi.resetAllMocks();
    vi.unstubAllGlobals();
    vi.unstubAllEnvs();
});

const { getAccessTokenRawMock } = vi.hoisted(() => ({
    getAccessTokenRawMock: vi.fn(),
}));

vi.mock("@kinde-oss/kinde-auth-nextjs/server", () => {
    return {
        getKindeServerSession: vi.fn(() => ({
            getAccessTokenRaw: getAccessTokenRawMock,
        })),
    };
});

const { redirectMock } = vi.hoisted(() => ({
    redirectMock: vi.fn(),
}));

vi.mock("next/navigation", () => {
    return {
        redirect: redirectMock,
    };
});

test("normal case", async () => {
    getAccessTokenRawMock.mockResolvedValue("test-token");
    fetchMock.mockResolvedValue(new Response(null, { status: 200 }));

    const response = await backendFetch(
        "/identity/me",
        { method: "GET" },
        { redirectOnAuthError: false },
    );

    expect(response).toBeInstanceOf(Response);
    expect(response.status).toBe(200);
    expect(fetch).toHaveBeenCalledWith(
        `${process.env.BACKEND_URL}/identity/me`,
        {
            cache: "no-store",
            method: "GET",
            headers: expect.any(Headers),
        },
    );

    const options = fetchMock.mock.calls[0][1];

    expect(options?.headers).toBeInstanceOf(Headers);

    const headers = options?.headers as Headers;
    expect(headers.get("Authorization")).toBe("Bearer test-token");
});

test("should redirect on no token error", async () => {
    getAccessTokenRawMock.mockResolvedValue(null);

    await expect(
        backendFetch(
            "/identity/me",
            { method: "GET" },
            { redirectOnAuthError: true },
        ),
    ).rejects.toThrow("redirect:/api/auth/login");
    expect(redirectMock).toHaveBeenCalledExactlyOnceWith("/api/auth/login");
    expect(fetch).not.toHaveBeenCalled();
});

test("returns 401 without fetching when the token is absent and redirects are disabled", async () => {
    getAccessTokenRawMock.mockResolvedValue(null);
    const response = await backendFetch(
        "/identity/me",
        {},
        { redirectOnAuthError: false },
    );
    expect(response.status).toBe(401);
    expect(fetchMock).not.toHaveBeenCalled();
    expect(redirectMock).not.toHaveBeenCalled();
});

test("redirects to login when the backend rejects the token", async () => {
    getAccessTokenRawMock.mockResolvedValue("expired-token");
    fetchMock.mockResolvedValue(new Response(null, { status: 401 }));
    await expect(backendFetch("/identity/me")).rejects.toThrow(
        "redirect:/api/auth/login",
    );
    expect(redirectMock).toHaveBeenCalledExactlyOnceWith("/api/auth/login");
    expect(fetchMock).toHaveBeenCalledTimes(1);
});

test("redirects to onboarding when the backend explicitly requires it", async () => {
    getAccessTokenRawMock.mockResolvedValue("test-token");
    fetchMock.mockResolvedValue(
        Response.json({ code: "ONBOARDING_REQUIRED" }, { status: 403 }),
    );
    await expect(backendFetch("/identity/me")).rejects.toThrow(
        "redirect:/onboarding",
    );
    expect(redirectMock).toHaveBeenCalledExactlyOnceWith("/onboarding");
});

test.each([
    [401, "UNAUTHORIZED"],
    [403, "ONBOARDING_REQUIRED"],
])("returns HTTP %i unchanged when redirects are disabled", async (status, code) => {
    getAccessTokenRawMock.mockResolvedValue("test-token");
    const backendResponse = Response.json({ code }, { status });
    fetchMock.mockResolvedValue(backendResponse);
    const response = await backendFetch(
        "/identity/me",
        {},
        { redirectOnAuthError: false },
    );
    expect(response).toBe(backendResponse);
    expect(await response.json()).toEqual({ code });
    expect(redirectMock).not.toHaveBeenCalled();
});

test("preserves an unrelated 403 and leaves its body readable", async () => {
    getAccessTokenRawMock.mockResolvedValue("test-token");
    const body = { code: "FORBIDDEN" };
    const backendResponse = Response.json(body, { status: 403 });
    fetchMock.mockResolvedValue(backendResponse);
    const response = await backendFetch("/identity/me");
    expect(response).toBe(backendResponse);
    expect(await response.json()).toEqual(body);
    expect(redirectMock).not.toHaveBeenCalled();
});

test("preserves a non-JSON 403 instead of failing while reading the error", async () => {
    getAccessTokenRawMock.mockResolvedValue("test-token");
    fetchMock.mockResolvedValue(new Response("Forbidden", { status: 403 }));
    const response = await backendFetch("/identity/me");
    expect(response.status).toBe(403);
    expect(await response.text()).toBe("Forbidden");
    expect(redirectMock).not.toHaveBeenCalled();
});

test("returns a backend failure without redirecting", async () => {
    getAccessTokenRawMock.mockResolvedValue("test-token");
    const backendResponse = new Response("Unavailable", { status: 500 });
    fetchMock.mockResolvedValue(backendResponse);
    expect(await backendFetch("/identity/me")).toBe(backendResponse);
    expect(redirectMock).not.toHaveBeenCalled();
});

test("propagates network errors to the caller", async () => {
    getAccessTokenRawMock.mockResolvedValue("test-token");
    const error = new TypeError("Failed to fetch");
    fetchMock.mockRejectedValue(error);
    await expect(backendFetch("/identity/me")).rejects.toBe(error);
    expect(redirectMock).not.toHaveBeenCalled();
});

test("preserves request options and headers while using the session token", async () => {
    getAccessTokenRawMock.mockResolvedValue("test-token");
    fetchMock.mockResolvedValue(new Response(null, { status: 201 }));
    const body = new URLSearchParams({ username: "Camille" });
    const signal = new AbortController().signal;
    await backendFetch("/identity/register", {
        method: "POST",
        body,
        signal,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            Authorization: "Bearer old-token",
        },
    });
    expect(fetchMock).toHaveBeenCalledExactlyOnceWith(
        "http://localhost:8080/identity/register",
        {
            method: "POST",
            body,
            signal,
            cache: "no-store",
            headers: expect.any(Headers),
        },
    );
    const headers = fetchMock.mock.calls[0][1]?.headers as Headers;
    expect(headers.get("Content-Type")).toBe(
        "application/x-www-form-urlencoded",
    );
    expect(headers.get("Authorization")).toBe("Bearer test-token");
});
