import { withAuth } from "@kinde-oss/kinde-auth-nextjs/middleware";
import { NextRequest } from "next/dist/server/web/spec-extension/request";

export default withAuth(
  async function proxy(req: NextRequest) {
  },
  {
    // Proxy still runs on all routes, but doesn't protect the home route
    publicPaths: ["/", "/auth/error"], // e.g. ["/api/public", "/blog", "/about"]
	isReturnToCurrentPage: true,
  }
);

export const config = {
  matcher: [
    '/((?!_next|[^?]*\\.(?:html?|css|js(?!on)|jpe?g|webp|png|gif|svg|ttf|woff2?|ico|csv|docx?|xlsx?|zip|webmanifest)).*)',
  ],
}
