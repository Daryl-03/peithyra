import { afterAll, afterEach, describe, expect, test, vi } from "vitest";
import { getLatestDebates } from "./api";

const fetchMock = vi.fn<typeof fetch>();
vi.stubGlobal("fetch", fetchMock);

afterEach(() => {
    vi.resetAllMocks();
});

afterAll(() => {
    vi.unstubAllGlobals();
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
