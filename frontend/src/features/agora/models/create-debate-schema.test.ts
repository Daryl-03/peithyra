import { describe, expect, test } from "vitest";
import {
    createDebateFormSchema,
    defaultCreateDebateValues,
} from "./create-debate-schema";
import { DebateSide } from "./debate-side";

const valid = {
    ...defaultCreateDebateValues,
    proposition: "Une proposition à débattre",
    description: "Une description suffisamment longue.",
};

describe("createDebateFormSchema", () => {
    test.each([
        DebateSide.For,
        DebateSide.Against,
    ])("accepts camp %s and trims text", (side) => {
        expect(
            createDebateFormSchema.parse({
                ...valid,
                side,
                proposition: `  ${valid.proposition}  `,
            }),
        ).toEqual({ ...valid, side });
    });

    test.each([
        { proposition: "a".repeat(14) },
        { proposition: "a".repeat(101) },
        { proposition: " ".repeat(20) },
        { description: "a".repeat(9) },
        { description: "a".repeat(201) },
        { side: "For" },
        { side: "Against" },
        { side: "" },
        { numberOfTurns: 0 },
        { numberOfTurns: 11 },
        { numberOfTurns: 1.5 },
        { numberOfTurns: "3" },
        { turnTimeLimitSeconds: 59 },
        { turnTimeLimitSeconds: 601 },
        { turnTimeLimitSeconds: 60.5 },
        { turnTimeLimitSeconds: Number.NaN },
        { turnTimeLimitSeconds: "180" },
    ])("rejects invalid input: %j", (input) => {
        expect(
            createDebateFormSchema.safeParse({ ...valid, ...input }).success,
        ).toBe(false);
    });

    test.each([
        60, 180, 600, 125,
    ])("accepts preset or custom duration %i", (turnTimeLimitSeconds) => {
        expect(
            createDebateFormSchema.safeParse({ ...valid, turnTimeLimitSeconds })
                .success,
        ).toBe(true);
    });

    test.each([1, 10])("accepts turn boundary %i", (numberOfTurns) => {
        expect(
            createDebateFormSchema.safeParse({ ...valid, numberOfTurns })
                .success,
        ).toBe(true);
    });
});
