import { fileURLToPath } from "node:url";
import react from "@vitejs/plugin-react";
import tsconfigPaths from "vite-tsconfig-paths";
import { defineConfig } from "vitest/config";

export default defineConfig({
    plugins: [tsconfigPaths(), react()],
    test: {
        environment: "jsdom",
        alias: {
            "server-only": fileURLToPath(
                new URL("./src/test/server-only.ts", import.meta.url),
            ),
        },
    },
});
