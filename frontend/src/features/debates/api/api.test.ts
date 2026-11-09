import { afterEach, describe, expect, test, vi } from "vitest";
import { backendFetch } from "@/lib/apis/backend-fetch";
import { getDebateById, getLatestDebates } from "./api";
import type { DebateDetailsDTO } from "./debate-details-dto";

vi.mock("@/lib/apis/backend-fetch", () => ({
    backendFetch: vi.fn(),
}));

const fetchMock = vi.mocked(backendFetch);

afterEach(() => {
    vi.resetAllMocks();
    vi.restoreAllMocks();
});

describe("getLatestDebates", () => {
    test("should extract the debate summary response correctly", async () => {
        fetchMock.mockResolvedValue(
            new Response(
                JSON.stringify({
                    content: [
                        {
                            id: "a1b2c3d4e5f6g7h8i9j0",
                            proposition: "Test Proposition",
                            status: "WAITING_FOR_OPPONENT",
                            createdAt: "2023-01-01T00:00:00Z",
                            participantViewList: [
                                {
                                    id: 1,
                                    username: "user1",
                                    side: "FOR",
                                },
                            ],
                        },
                    ],
                    page: 0,
                    size: 5,
                    totalElements: 0,
                    totalPages: 0,
                }),
                {
                    status: 200,
                    headers: {
                        "Content-Type": "application/json",
                    },
                },
            ),
        );

        const debates = await getLatestDebates(5);
        expect(debates).toHaveLength(1);
        expect(debates[0]).toEqual({
            id: "a1b2c3d4e5f6g7h8i9j0",
            proposition: "Test Proposition",
            status: "WAITING_FOR_OPPONENT",
            createdAt: "2023-01-01T00:00:00Z",
            participants: [
                {
                    id: 1,
                    username: "user1",
                    side: "FOR",
                },
            ],
        });
    });

    test("should throw an error when the fetch fails", async () => {
        fetchMock.mockResolvedValue(
            new Response(null, {
                status: 500,
                statusText: "Internal Server Error",
            }),
        );

        await expect(getLatestDebates(5)).rejects.toThrow();
    });
});

describe("getDebateById", () => {
    const debateId = "a1b2c3d4e5f6g7h8i9j0";

    test("should request the debate by ID and return its details", async () => {
        const debate: DebateDetailsDTO = {
            id: debateId,
            proposition: "Test Proposition",
            description: "Test description",
            status: "WAITING_FOR_OPPONENT",
            format: {
                numberOfTurns: 3,
                turnTimeLimitSeconds: 120,
            },
            createdAt: "2026-11-09T12:00:00Z",
            participants: [{ id: "user-1", username: "user1", side: "FOR" }],
        };
        fetchMock.mockResolvedValue(Response.json(debate));

        await expect(getDebateById(debateId)).resolves.toEqual(debate);
        expect(fetchMock).toHaveBeenCalledExactlyOnceWith(
            `/api/debates/${debateId}`,
        );
    });

    test.each([
        [404, "Not Found"],
        [500, "Internal Server Error"],
    ])("should reject when the backend returns %s", async (status, statusText) => {
        vi.spyOn(console, "error").mockImplementation(() => {});
        fetchMock.mockResolvedValue(new Response(null, { status, statusText }));

        await expect(getDebateById(debateId)).rejects.toThrow(
            `Failed to fetch debate with ID ${debateId}: ${statusText}`,
        );
    });

    test("should propagate a backend request failure", async () => {
        vi.spyOn(console, "error").mockImplementation(() => {});
        const error = new TypeError("Network request failed");
        fetchMock.mockRejectedValue(error);

        await expect(getDebateById(debateId)).rejects.toBe(error);
    });

    test("should reject when the response is not valid JSON", async () => {
        vi.spyOn(console, "error").mockImplementation(() => {});
        fetchMock.mockResolvedValue(
            new Response("invalid JSON", { status: 200 }),
        );

        await expect(getDebateById(debateId)).rejects.toBeInstanceOf(
            SyntaxError,
        );
    });
});
