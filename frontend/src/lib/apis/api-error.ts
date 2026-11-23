export const DEFAULT_API_ERROR_MESSAGE =
    "Le service est momentanément indisponible. Veuillez réessayer.";

export class ApiError extends Error {
    constructor(
        public readonly code: string,
        message: string,
        public readonly status: number,
        options?: ErrorOptions,
    ) {
        super(message, options);
        this.name = "ApiError";
    }

    static async fromResponse(response: Response): Promise<ApiError> {
        const body: unknown = await response.json().catch(() => null);
        let code = "UNKNOWN_ERROR";
        let message = DEFAULT_API_ERROR_MESSAGE;
        if (typeof body === "object" && body !== null) {
            if (
                "code" in body &&
                typeof body.code === "string" &&
                body.code.trim()
            ) {
                code = body.code;
            }
            if (
                "message" in body &&
                typeof body.message === "string" &&
                body.message.trim()
            ) {
                message = body.message;
            }
        }
        const error = new ApiError(code, message, response.status);
        console.error("Backend request failed:", {
            url: response.url,
            status: error.status,
            code: error.code,
            message: error.message,
        });
        return error;
    }

    static fromUnknown(cause: unknown): ApiError {
        if (cause instanceof ApiError) return cause;
        const error = new ApiError(
            "UNKNOWN_ERROR",
            DEFAULT_API_ERROR_MESSAGE,
            500,
            { cause },
        );
        console.error("Unexpected API error:", error);
        return error;
    }
}
