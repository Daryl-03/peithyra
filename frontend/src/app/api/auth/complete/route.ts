import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server";

export async function GET(request: Request) {
	const { getUser, getAccessTokenRaw } = getKindeServerSession();
	const user = await getUser();

	// call the backend API to get the user info
	const response = await fetch(
		`${process.env.BACKEND_URL}/identity/me`,
		{
			method: "GET",
			headers: {
				"Content-Type": "application/json",
				Authorization: `Bearer ${await getAccessTokenRaw()}`,
			},
		}
	);

	if (!response.ok) {
		switch (response.status) {
			case 403:
				// check code in body
				const data = await response.json();
				console.log("data", data);
				if (data.code === "ONBOARDING_REQUIRED") {
					return new Response(null, {
						status: 302,
						headers: {
							Location: "/onboarding",
						},
					});
				}
				break;
			default:
				break;
		}
	} else {
		const data = await response.json();
		console.log("data", data);
		return new Response(null, {
			status: 302,
			headers: {
				Location: "/",
			},
		});
	}
}