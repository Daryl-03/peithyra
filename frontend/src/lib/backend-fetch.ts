import "server-only";
import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server";



export async function backendFetch(path: string, options: RequestInit = {}): Promise<Response> {
	const { getAccessTokenRaw } = getKindeServerSession();
	const token = await getAccessTokenRaw();
	const url = `${process.env.BACKEND_URL}${path}`;
	const headers = new Headers(options.headers || {});
	headers.set("Authorization", `Bearer ${token}`);

	return fetch(url, { ...options, headers });
}