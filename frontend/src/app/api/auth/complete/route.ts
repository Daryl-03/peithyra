import { getKindeServerSession } from "@kinde-oss/kinde-auth-nextjs/server";

export async function GET(request: Request) {
	const { getUser } = getKindeServerSession();
	const user = await getUser();

	// call the backend API to get the user info
	// const response = await fetch(
	// 	`${process.env.BACKEND_URL}/users/me`,
	// 	{
	// 		method: "GET",
	// 		headers: {
	// 			"Content-Type": "application/json",
	// 		},
	// 	}
	// );

	// const userData = await response.json();
	// if(!userData.username ) {
	// 	return new Response(null, {
	// 		status: 302,
	// 		headers: {
	// 			Location: "/onboarding",
	// 		},
	// 	});
	// }

}